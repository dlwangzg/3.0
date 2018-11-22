/**
 *
 */
package com.leadingsoft.bizfuse.quartz.core.scheduler;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leadingsoft.bizfuse.quartz.core.CommonSchedulerJob;
import com.leadingsoft.bizfuse.quartz.core.DisallowConcurrentExectionJob;
import com.leadingsoft.bizfuse.quartz.core.JobRunnable;
import com.leadingsoft.bizfuse.quartz.core.annotition.JobMapping;
import com.leadingsoft.bizfuse.quartz.core.listeners.CronTriggerScheduleListener;
import com.leadingsoft.bizfuse.quartz.core.managment.JobConcurrentInfoHolder;
import com.leadingsoft.bizfuse.quartz.core.managment.JobsManager;
import com.leadingsoft.bizfuse.quartz.utils.ParamsSerializer;

/**
 * 公共的Job执行器。
 *
 * <pre>
 * 所有<code>{@link JobMapping}</code>注释的方法都可以被该执行器调用
 * 实现<code>{@link JobRunnable}</code>接口的类，其接口方法可以被该执行器调用
 * </pre>
 *
 * @author liuyg
 * @version 1.0
 */
@Component
public class CommonJobsExecutor {

    /** logger */
    private static final Log LOGGER = LogFactory.getLog("JOBLog");

    /** Spring容器维护的Job派发器 */
    @Resource
    private Scheduler scheduler;
    @Resource
    private ParamsSerializer paramsSerializer;

    /**
     * 定时触发器派发监听器（内存方式的Quartz调度，需要程序实现服务再启动时的Job恢复，所以要监听每个新触发器的使用；集群方式不需要该监听）
     */
    private Set<CronTriggerScheduleListener> cronTriggerScheduleListeners;

    private JobConcurrentInfoHolder jobConcurrentInfoHolder;

    public void addJob(final String jobId) throws SchedulerException {
        if (this.scheduler.checkExists(new JobKey(jobId))) {
            return;
        }
        final JobDetail detail = this.createJobDetail(new JobKey(jobId));
        this.scheduler.addJob(detail, false);
    }

    /**
     * 派发定时触发的Job。
     *
     * @param jobId jobId
     * @param triggerId 触发器Id
     * @param cronExpression 定时触发规则
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public void scheduleJob(final String jobId, final String triggerId, final String cronExpression)
            throws SchedulerException {
        this.scheduleJob(jobId, triggerId, cronExpression, null, null);
    }

    /**
     * 派发定时触发的Job。
     *
     * @param jobId jobId
     * @param triggerId 触发器Id
     * @param cronExpression 定时触发规则
     * @param triggerParams 参数
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public void scheduleJob(final String jobId, final String triggerId, final String cronExpression,
            final Object... triggerParams) throws SchedulerException {
        this.scheduleJob(jobId, triggerId, cronExpression, triggerParams, null);
    }

    /**
     * 派发定时触发的Job。
     *
     * @param jobId jobId
     * @param triggerId 触发器Id
     * @param cronExpression 定时触发规则
     * @param params 参数
     * @param paramTypes 参数类型（解决泛型擦除）
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public void scheduleJob(final String jobId, final String triggerId, final String cronExpression,
            final Object[] params, final TypeReference<?>[] paramTypes) throws SchedulerException {
        final Trigger cronTrigger = this.createCronTrigger(jobId, new TriggerKey(triggerId), cronExpression);
        this.scheduleJob(cronTrigger, params, paramTypes);
    }

    /**
     * 派发一个指定了触发器的Job。
     *
     * @param trigger 触发器
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public void scheduleJob(final Trigger trigger) throws SchedulerException {
        this.scheduleJob(trigger, null, null);
    }

    /**
     * 派发一个指定了触发器的Job。
     *
     * @param trigger 触发器
     * @param params 参数
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public void scheduleJob(final Trigger trigger, final Object... params) throws SchedulerException {
        this.scheduleJob(trigger, params, null);
    }

    /**
     * 派发一个指定了触发器的Job。
     *
     * @param trigger 触发器
     * @param params 参数
     * @param paramTypes 参数类型
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public void scheduleJob(final Trigger trigger, final Object[] params, final TypeReference<?>[] paramTypes)
            throws SchedulerException {

        // 设置参数
        final JobDataMap jobDataMap = trigger.getJobDataMap();
        final String jobId = trigger.getJobKey().getName();
        Assert.hasLength(jobId);
        jobDataMap.put(JobsManager.JOB_ID, jobId);
        this.paramsSerializer.serializeParamsToMap(jobDataMap, jobId, params, paramTypes);

        // 已经存在相同的触发器
        final boolean isTriggerExists = this.checkTriggerExists(trigger.getKey());
        final Trigger oldTrigger = isTriggerExists ? this.scheduler.getTrigger(trigger.getKey()) : null;
        // JOB并发属性变更
        final boolean isAllowCurrencyChanged = isTriggerExists && this.isAllowCurrencyChanged(oldTrigger, trigger);

        if (!isTriggerExists || isAllowCurrencyChanged) {
            // 如果JOB并发属性变更 删除原JOB
            if (isAllowCurrencyChanged) {
                this.removeJob(trigger.getJobKey());
            }

            if (this.scheduler.checkExists(trigger.getJobKey())) {
                this.scheduler.scheduleJob(trigger);
            } else {
                this.scheduler.scheduleJob(this.createJobDetail(trigger.getJobKey()), trigger);
            }

            this.fireTriggerScheduleEvent(trigger);
            if (CommonJobsExecutor.LOGGER.isInfoEnabled()) {
                CommonJobsExecutor.LOGGER.info(String.format(
                        "Job started... Job's id is [%s], Trigger's id is [%s]", trigger.getJobKey(),
                        trigger.getKey()));
            }
        } else if (this.isTriggerChanged(oldTrigger, trigger)) { // 触发器变更的
            this.scheduler.rescheduleJob(oldTrigger.getKey(), trigger);
            if (CommonJobsExecutor.LOGGER.isInfoEnabled()) {
                CommonJobsExecutor.LOGGER.info("Trigger rescheduled, Job key:" + oldTrigger.getJobKey().getName());
            }
        }
    }

    /**
     * 立即派发一个新的Job，该Job执行完后不会驻留在派发器中（无参数）。
     *
     * @param jobId job标识
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public void scheduleJobImmediately(final String jobId) throws SchedulerException {
        this.scheduleJob(jobId, 0, null, null);
    }

    /**
     * 派发一个新的Job，该Job执行完后不会驻留在派发器中（无参数）。
     *
     * @param jobId job标识
     * @param delaySeconds 延迟秒数
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public void scheduleJob(final String jobId, final int delaySeconds) throws SchedulerException {
        this.scheduleJob(jobId, delaySeconds, null, null);
    }

    /**
     * 立即派发一个新的Job，该Job执行完后不会驻留在派发器中（针对非泛型参数）。
     *
     * @param jobId job标识
     * @param params 参数（由于存在泛型擦除，该方法不能使用泛型类型的参数）
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public void scheduleJobImmediately(final String jobId, final Object... params) throws SchedulerException {
        this.scheduleJob(jobId, 0, params, null);
    }

    /**
     * 派发一个新的Job，该Job执行完后不会驻留在派发器中（针对非泛型参数）。
     *
     * @param jobId job标识
     * @param delaySeconds 延迟秒数
     * @param params 参数（由于存在泛型擦除，该方法不能使用泛型类型的参数）
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public void scheduleJob(final String jobId, final int delaySeconds, final Object... params)
            throws SchedulerException {
        this.scheduleJob(jobId, delaySeconds, params, null);
    }

    /**
     * 派发一个新的Job，该Job执行完后不会驻留在派发器中（针对泛型参数）。
     *
     * @param jobId job标识
     * @param params 参数
     * @param paramTypes 参数的类型引用数组（用于处理泛型类型的参数）
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public void scheduleJobImmediately(final String jobId, final Object[] params,
            final TypeReference<?>[] paramTypes) throws SchedulerException {

        this.scheduleJob(jobId, 0, params, paramTypes);
    }

    /**
     * 派发一个新的Job，该Job执行完后不会驻留在派发器中（针对泛型参数）。
     *
     * @param jobId job标识
     * @param delaySeconds 延迟秒数
     * @param params 参数
     * @param paramTypes 参数的类型引用数组（用于处理泛型类型的参数）
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public void scheduleJob(final String jobId, final int delaySeconds, final Object[] params,
            final TypeReference<?>[] paramTypes) throws SchedulerException {

        final Trigger trigger = this.createTrigger(jobId, null, null, delaySeconds);

        this.scheduleJob(trigger, params, paramTypes);
    }

    /**
     * 移除一个Job触发器。
     *
     * @param triggerKey triggerKey
     * @throws SchedulerException
     */
    public void removeJobTrigger(final TriggerKey triggerKey) throws SchedulerException {
        if (this.scheduler.checkExists(triggerKey)) {
            final Trigger trigger = this.scheduler.getTrigger(triggerKey);
            this.scheduler.unscheduleJob(triggerKey);
            this.fireTriggerRemovedEvent(trigger);
        }
    }

    /**
     * 移除指定的Job。
     *
     * @param jobKey jobKey
     * @throws SchedulerException Job移除过程中出现了异常
     */
    public void removeJob(final JobKey jobKey) throws SchedulerException {
        this.scheduler.deleteJob(jobKey);
    }

    /**
     * 清空所有Job。
     *
     * @throws SchedulerException Job移除过程中出现了异常
     */
    public void removeAllJobs() throws SchedulerException {
        this.scheduler.clear();
    }

    /**
     * 重新派发Job
     *
     * @param triggerKey triggerKey
     * @param newTrigger newTrigger
     * @throws SchedulerException
     */
    public void rescheduleJob(final TriggerKey triggerKey, final Trigger newTrigger) throws SchedulerException {
        this.scheduler.rescheduleJob(triggerKey, newTrigger);
    }

    /**
     * 生成定时触发器。
     *
     * @param jobId jobId
     * @param triggerKey triggerKey
     * @param cronExpression cronExpression
     * @return 定时触发器
     */
    public Trigger createCronTrigger(final String jobId, final TriggerKey triggerKey, final String cronExpression) {
        return this.createCronTrigger(jobId, triggerKey, cronExpression, null);
    }

    /**
     * 生成定时触发器。
     *
     * @param jobId jobId
     * @param triggerKey triggerKey
     * @param cronExpression cronExpression
     * @param jobDataMap 参数
     * @return 定时触发器
     */
    public Trigger createCronTrigger(final String jobId, final TriggerKey triggerKey, final String cronExpression,
            final JobDataMap jobDataMap) {
        Assert.hasLength(cronExpression);
        final JobKey jobKey = new JobKey(jobId);
        // 构造触发器
        final CronTriggerImpl cronTriggerBean = new CronTriggerImpl();
        cronTriggerBean.setKey(triggerKey);
        cronTriggerBean.setJobKey(jobKey);
        try {
            cronTriggerBean.setCronExpression(cronExpression);
        } catch (final ParseException e) {
            CommonJobsExecutor.LOGGER.error(e);
            throw new RuntimeException(e);
        }
        if (jobDataMap != null) {
            cronTriggerBean.setJobDataMap(jobDataMap);
        }
        return cronTriggerBean;
    }

    /**
     * 创建基本触发器。
     *
     * @param jobId jobId
     * @param name 触发器名
     * @param group 组名
     * @param delaySeconds 延迟秒数
     * @return 触发器实例
     */
    public Trigger createTrigger(final String jobId, final String name, final String group, final int delaySeconds) {
        final double disturbedTime = Math.random(); // 干扰时间(0.0, 0.1)秒的随机数，防止同一时刻调度很多Job
        // Job 触发时间
        Date startTime = null;
        if (delaySeconds > 0) {
            // 触发时刻（毫秒） = 当前时刻 + 延迟毫秒 - 干扰毫秒
            final long runTime = (new Date().getTime() + (1000L * delaySeconds)) - (long) (100L * disturbedTime);
            startTime = new Date(runTime);
        } else {
            // 触发时刻（毫秒） = 当前时刻 + 干扰毫秒       干扰时间控制在(0.0, 0.01)秒
            startTime = new Date(new Date().getTime() + (long) (10L * disturbedTime));
        }

        // triggerName = uuid 防止触发器名冲突
        String triggerName = name;
        if (!StringUtils.hasText(triggerName)) {
            triggerName = UUID.randomUUID().toString();
        }
        final SimpleTriggerImpl simpleTrigger = new SimpleTriggerImpl();

        simpleTrigger.setName(triggerName);
        simpleTrigger.setGroup(group);
        simpleTrigger.setStartTime(startTime);
        simpleTrigger.setRepeatCount(0);
        simpleTrigger.setRepeatInterval(0);
        simpleTrigger.setJobKey(new JobKey(jobId));
        return simpleTrigger;
    }

    /**
     * 创建通用的Job明细。
     *
     * @param jobKey jobKey
     * @return Job明细
     */
    public JobDetail createJobDetail(final JobKey jobKey) {
        return this.createJobDetail(jobKey, null);
    }

    /**
     * 创建Job明细。
     *
     * @param jobKey jobKey
     * @param jobClass Job类
     * @return Job明细
     */
    public <T extends Job> JobDetail createJobDetail(final JobKey jobKey, final Class<T> jobClass) {

        final JobDetailImpl jobDetail = new JobDetailImpl();
        jobDetail.setJobClass(jobClass != null ? jobClass : this.getCommonJobClass(jobKey));
        jobDetail.setKey(jobKey);
        jobDetail.setDurability(true);
        return jobDetail;
    }

    /**
     * 关闭Job管理器
     */
    public void shutdownScheduledJobs() {
        try {
            if (CommonJobsExecutor.LOGGER.isInfoEnabled()) {
                CommonJobsExecutor.LOGGER.info("================shutdown scheduler=================");
            }
            this.scheduler.shutdown(true);
        } catch (final SchedulerException e) {
            CommonJobsExecutor.LOGGER.error(e);
        }
    }

    /**
     * 检查Job是否存在。
     *
     * @param jobKey jobKey
     * @return true if job is existing.
     * @throws SchedulerException
     */
    public boolean checkJobExists(final JobKey jobKey) throws SchedulerException {
        return this.scheduler.checkExists(jobKey);
    }

    /**
     * 检查触发器是否存在。
     *
     * @param triggerKey triggerKey
     * @return true if trigger is existing.
     * @throws SchedulerException
     */
    public boolean checkTriggerExists(final TriggerKey triggerKey) throws SchedulerException {
        return this.scheduler.checkExists(triggerKey);
    }

    /**
     * 获取调度器实例。
     *
     * @return 调度器实例
     */
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    /**
     * @param scheduler scheduler
     */
    public void setScheduler(final Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 注册一个定时触发器的派发监听器。
     *
     * @param listener 定时触发器的派发监听器
     */
    public void registerCronTriggerScheduleListener(final CronTriggerScheduleListener listener) {
        if (this.cronTriggerScheduleListeners == null) {
            this.cronTriggerScheduleListeners = new HashSet<CronTriggerScheduleListener>(5);
        }
        this.cronTriggerScheduleListeners.add(listener);
    }

    /**
     * 取要触发的通用Job类。
     *
     * @return Job的类
     */
    protected Class<? extends Job> getCommonJobClass(final JobKey jobKey) {
        boolean isAllowConcurrentExecute = true;
        if (this.jobConcurrentInfoHolder != null) {
            isAllowConcurrentExecute = this.jobConcurrentInfoHolder.isAllowConcurrentExecute(jobKey.getName());
        }
        if (isAllowConcurrentExecute) {
            return CommonSchedulerJob.class;
        } else {
            return DisallowConcurrentExectionJob.class;
        }
    }

    /**
     * @param trigger
     */
    private void fireTriggerScheduleEvent(final Trigger trigger) {
        if ((this.cronTriggerScheduleListeners == null) || (this.cronTriggerScheduleListeners.size() == 0)) {
            return;
        }
        if (trigger instanceof org.quartz.CronTrigger) {
            for (final CronTriggerScheduleListener listener : this.cronTriggerScheduleListeners) {
                listener.registerCronTrigger((org.quartz.CronTrigger) trigger);
            }
        }
    }

    /**
     * @param trigger
     */
    private void fireTriggerRemovedEvent(final Trigger trigger) {
        if ((this.cronTriggerScheduleListeners == null) || (this.cronTriggerScheduleListeners.size() == 0)) {
            return;
        }
        if (trigger instanceof org.quartz.CronTrigger) {
            for (final CronTriggerScheduleListener listener : this.cronTriggerScheduleListeners) {
                listener.removeCronTrigger((org.quartz.CronTrigger) trigger);
            }
        }
    }

    /**
     * @param jobConcurrentInfoHolder jobConcurrentInfoHolder
     */
    public void setJobConcurrentInfoHolder(final JobConcurrentInfoHolder jobConcurrentInfoHolder) {
        this.jobConcurrentInfoHolder = jobConcurrentInfoHolder;
    }

    private boolean isTriggerChanged(final Trigger oldTrigger, final Trigger newTrigger) throws SchedulerException {
        if (oldTrigger.getClass() != newTrigger.getClass()) {
            return true;
        }
        if (oldTrigger instanceof CronTriggerImpl) {
            final CronTriggerImpl oldCronTrigger = (CronTriggerImpl) oldTrigger;
            final CronTriggerImpl newCronTrigger = (CronTriggerImpl) newTrigger;
            return !oldCronTrigger.getCronExpression().equals(newCronTrigger.getCronExpression());
        } else if (oldTrigger instanceof SimpleTriggerImpl) {
            final SimpleTriggerImpl oldSimpleTrigger = (SimpleTriggerImpl) oldTrigger;
            final SimpleTriggerImpl newSimpleTrigger = (SimpleTriggerImpl) newTrigger;
            return (oldSimpleTrigger.getRepeatInterval() != newSimpleTrigger.getRepeatInterval()) || (oldSimpleTrigger
                    .getRepeatCount() != newSimpleTrigger.getRepeatCount());
        } else {
            return false;
        }
    }

    /**
     * Job 并发属性是否变更
     *
     * @param oldTrigger
     * @param newTrigger
     * @return
     * @throws SchedulerException
     */
    private boolean isAllowCurrencyChanged(final Trigger oldTrigger, final Trigger newTrigger)
            throws SchedulerException {
        final Class<?> oldJobClass = this.scheduler.getJobDetail(oldTrigger.getJobKey()).getJobClass();
        final Class<?> newJobClass = this.getCommonJobClass(newTrigger.getJobKey());
        return oldJobClass != newJobClass;
    }
}
