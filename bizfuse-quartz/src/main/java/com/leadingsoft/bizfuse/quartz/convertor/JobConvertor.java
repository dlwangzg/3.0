package com.leadingsoft.bizfuse.quartz.convertor;

import java.util.List;

import javax.annotation.Resource;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.quartz.dto.JobDetailDTO;

@Component
public class JobConvertor extends AbstractConvertor<JobKey, JobDetailDTO> {

    @Resource
    private Scheduler scheduler;

    @Override
    public JobDetailDTO toDTO(final JobKey jobKey) {
        final JobDetailDTO jobDetailDTO = new JobDetailDTO();
        JobDetail jobDetail;
        try {
            jobDetail = this.scheduler.getJobDetail(jobKey);
        } catch (final SchedulerException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        jobDetailDTO.setJobKey(jobDetail.getKey().toString());
        jobDetailDTO.setJobGroup(jobDetail.getKey().getGroup());
        jobDetailDTO.setJobName(jobDetail.getKey().getName());
        jobDetailDTO.setDescription(jobDetail.getDescription());
        jobDetailDTO.setDurable(jobDetail.isDurable());

        try {
            final List<? extends Trigger> list = this.scheduler.getTriggersOfJob(jobDetail.getKey());
            boolean paused = true;
            for (final Trigger trigger : list) {
                final TriggerState state = this.scheduler.getTriggerState(trigger.getKey());
                if (TriggerState.NORMAL.equals(state)) {
                    paused = false;
                }
            }
            jobDetailDTO.setPaused(paused);
            jobDetailDTO.setTriggerNum(list.size());
        } catch (final SchedulerException e) {
            e.printStackTrace();
        }
        return jobDetailDTO;
    }

    @Override
    public JobKey toModel(final JobDetailDTO dto) {
        // TODO Auto-generated method stub
        return null;
    }
}
