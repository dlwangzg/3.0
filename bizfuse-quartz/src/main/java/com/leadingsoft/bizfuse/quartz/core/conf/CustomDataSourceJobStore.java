package com.leadingsoft.bizfuse.quartz.core.conf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.JobPersistenceException;
import org.quartz.TriggerKey;
import org.quartz.impl.jdbcjobstore.Constants;
import org.quartz.spi.OperableTrigger;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;

import com.leadingsoft.bizfuse.quartz.core.managment.JobMappingHolder;

public class CustomDataSourceJobStore extends LocalDataSourceJobStore {

    @Override
    protected List<OperableTrigger> acquireNextTrigger(final Connection conn, final long noLaterThan,
            final int maxCount, final long timeWindow)
            throws JobPersistenceException {
        if (timeWindow < 0) {
            throw new IllegalArgumentException();
        }

        final List<OperableTrigger> acquiredTriggers = new ArrayList<OperableTrigger>();
        final Set<JobKey> acquiredJobKeysForNoConcurrentExec = new HashSet<JobKey>();
        final int MAX_DO_LOOP_RETRY = 3;
        int currentLoopCount = 0;
        long firstAcquiredTriggerFireTime = 0;

        do {
            currentLoopCount++;
            try {
                final List<TriggerKey> keys = this.getDelegate().selectTriggerToAcquire(conn, noLaterThan + timeWindow,
                        this.getMisfireTime(), maxCount);
                // No trigger is ready to fire yet.
                if ((keys == null) || (keys.size() == 0)) {
                    return acquiredTriggers;
                }

                for (final TriggerKey triggerKey : keys) {
                    // If our trigger is no longer available, try a new one.
                    final OperableTrigger nextTrigger = this.retrieveTrigger(conn, triggerKey);
                    if (nextTrigger == null) {
                        continue; // next trigger
                    }

                    // If trigger's job is set as @DisallowConcurrentExecution, and it has already been added to result, then
                    // put it back into the timeTriggers set and continue to search for next trigger.
                    final JobKey jobKey = nextTrigger.getJobKey();
                    if (!JobMappingHolder.containsJob(jobKey.getName())) {
                        continue;
                    }
                    JobDetail job;
                    try {
                        job = this.retrieveJob(conn, jobKey);
                    } catch (final JobPersistenceException jpe) {
                        try {
                            this.getLog().error("Error retrieving job, setting trigger state to ERROR.", jpe);
                            this.getDelegate().updateTriggerState(conn, triggerKey, Constants.STATE_ERROR);
                        } catch (final SQLException sqle) {
                            this.getLog().error("Unable to set trigger state to ERROR.", sqle);
                        }
                        continue;
                    }

                    if (job.isConcurrentExectionDisallowed()) {
                        if (acquiredJobKeysForNoConcurrentExec.contains(jobKey)) {
                            continue; // next trigger
                        } else {
                            acquiredJobKeysForNoConcurrentExec.add(jobKey);
                        }
                    }

                    // We now have a acquired trigger, let's add to return list.
                    // If our trigger was no longer in the expected state, try a new one.
                    final int rowsUpdated = this.getDelegate().updateTriggerStateFromOtherState(conn, triggerKey,
                            Constants.STATE_ACQUIRED, Constants.STATE_WAITING);
                    if (rowsUpdated <= 0) {
                        continue; // next trigger
                    }
                    nextTrigger.setFireInstanceId(this.getFiredTriggerRecordId());
                    this.getDelegate().insertFiredTrigger(conn, nextTrigger, Constants.STATE_ACQUIRED, null);

                    acquiredTriggers.add(nextTrigger);
                    if (firstAcquiredTriggerFireTime == 0) {
                        firstAcquiredTriggerFireTime = nextTrigger.getNextFireTime().getTime();
                    }
                }

                // if we didn't end up with any trigger to fire from that first
                // batch, try again for another batch. We allow with a max retry count.
                if ((acquiredTriggers.size() == 0) && (currentLoopCount < MAX_DO_LOOP_RETRY)) {
                    continue;
                }

                // We are done with the while loop.
                break;
            } catch (final Exception e) {
                throw new JobPersistenceException(
                        "Couldn't acquire next trigger: " + e.getMessage(), e);
            }
        } while (true);

        // Return the acquired trigger list
        return acquiredTriggers;
    }
}
