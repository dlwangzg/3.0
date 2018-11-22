/**
 * 
 */
package com.leadingsoft.bizfuse.quartz.core.scheduler;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 公共的Job执行器。
 * 
 * <pre>
 * 可以继承该类实现自己的Job执行器
 * </pre>
 * 
 * @author liuyg
 * @version 1.0
 */
@Component
public class JobExecutor {
    /** Job传参的Key */
    public static String PARAM_KEY = "job.params.key";
    
    /** Spring容器维护的Job派发器 */
    @Resource
    private Scheduler scheduler;
    
    /**
     * 立即触发一个已配置的Job。
     * 
     * <pre>
     * 若该Job已经在派发器中，则触发原Job；若Job不存在，则抛出异常。
     * </pre>
     * 
     * @param name Job名
     * @param group 组名
     * @param params 参数
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public <T extends Serializable> void triggerJob(final String name, final String group, final T params)
            throws SchedulerException {
        Assert.hasText(name); // name not blank
        final Scheduler scheduler = this.getScheduler();
        final JobKey key = new JobKey(name, group);
        if (params != null) {
            final JobDataMap data = new JobDataMap();
            data.put(JobExecutor.PARAM_KEY, params);
            scheduler.triggerJob(key, data);
        } else {
            scheduler.triggerJob(key);
        }
    }
    
    /**
     * 派发一个新的Job，该Job执行完后不会驻留在派发器中。
     * 
     * <pre>
     * 调用该方法要求：子类必须实现<code>{@link getJobClass()}</code>方法。
     * </pre>
     * 
     * @param delaySeconds 延迟秒数
     * @param params 参数
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public <T extends Serializable> void scheduleJob(final int delaySeconds, final T params)
            throws SchedulerException {
        final Class<? extends Job> jobClass = this.getJobClass();
        this.scheduleJob(delaySeconds, params, jobClass);
    }
    
    /**
     * 派发一个新的Job，该Job执行完后不会驻留在派发器中。
     * 
     * @param delaySeconds 延迟秒数
     * @param params 参数
     * @param jobClass Job类
     * @throws SchedulerException Job派发过程中出现了异常
     */
    public <T extends Serializable> void scheduleJob(final int delaySeconds, final T params,
            final Class<? extends Job> jobClass) throws SchedulerException {
        this.scheduleJob(null, null, delaySeconds, params, jobClass);
    }
    
    /**
     * 派发一个新的Job，该Job执行完后不会驻留在派发器中。
     * 
     * <pre>
     * <li>创建JobDetail实例；执行JobDetail创建后的回调函数，用于设置Job参数等
     * <li>创建触发器实例；执行触发器创建后的回调函数，用于设置触发器参数等
     * <li>执行Job调度
     * <pre>
     * @param name Job名
     * @param group 组名
     * @param delaySeconds 延迟秒数
     * @param params 参数
     * @param jobClass 实现Job接口的类
     * @throws SchedulerException
     */
    public <T extends Serializable> void scheduleJob(final String name, final String group,
            final int delaySeconds, final T params, final Class<? extends Job> jobClass) throws SchedulerException {
        
        Assert.notNull(jobClass); // jobClass no null
        
        final Scheduler scheduler = this.getScheduler();
        
        final String groupName = !StringUtils.hasText(group) ? this.getDefaultGroup() : group;
        
        final JobDetail jobDetail = this.createJobDetail(name, groupName, jobClass, params);
        this.callBackOnCreatedJob(jobDetail);
        
        final Trigger trigger = this.createTrigger(name, groupName, delaySeconds);
        this.callBackOnCreatedTrigger(trigger);
        
        scheduler.scheduleJob(jobDetail, trigger);
    }
    
    /**
     * 获取调度器实例。
     * 
     * @return 调度器实例
     */
    protected Scheduler getScheduler() {
        // return StdSchedulerFactory.getDefaultScheduler();
        return this.scheduler;
    }
    
    /**
     * @param scheduler scheduler
     */
    protected void setScheduler(final Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    /**
     * 创建基本触发器。
     * 
     * @param name 触发器名
     * @param group 组名
     * @param delaySeconds 延迟秒数
     * @return 触发器实例
     */
    protected Trigger createTrigger(final String name, final String group, final int delaySeconds) {
        Date startTime = new Date();
        if (delaySeconds > 0) {
            final long oneSecond = 1000L;
            final long time = startTime.getTime() + (oneSecond * delaySeconds);
            startTime = new Date(time);
        }
        // triggerName = uuid 防止触发器名冲突
        String triggerName = name;
        if (!StringUtils.hasText(triggerName)) {
            triggerName = UUID.randomUUID().toString();
        }
        
        final SimpleTriggerImpl trigger = new SimpleTriggerImpl();
        trigger.setName(triggerName);
        trigger.setGroup(group);
        trigger.setStartTime(startTime);
        trigger.setRepeatCount(0);
        trigger.setRepeatInterval(0);
        return trigger;
    }
    
    /**
     * 创建Job明细。
     * 
     * @param name Job名
     * @param group 组名
     * @param jobClass Job类
     * @param params 参数
     * @return Job明细
     */
    protected <T extends Serializable> JobDetail createJobDetail(final String name, final String group,
            final Class<? extends Job> jobClass, final T params) {
        final JobDetailImpl jobDetail = new JobDetailImpl();
        // jobName = className + uuid 防止任务名冲突
        String jobName = name;
        if (!StringUtils.hasText(jobName)) {
            jobName = jobClass.getSimpleName() + UUID.randomUUID();
        }
        
        jobDetail.setJobClass(jobClass);
        jobDetail.setName(jobName);
        jobDetail.setGroup(group);
        
        if (params != null) {
            jobDetail.getJobDataMap().put(JobExecutor.PARAM_KEY, params);
        }
        return jobDetail;
    }
    
    /**
     * 取Job类。
     * 
     * @return Job类
     */
    protected Class<? extends Job> getJobClass() {
        throw new IllegalStateException("This method need to be overrided!");
    }
    
    /**
     * 取得Job组名。
     * 
     * @return Job分组名
     */
    protected String getDefaultGroup() {
        return null;
    }
    
    /**
     * 创建完JobDetail后的回调操作，可重写该方法设置Job参数。
     * 
     * @param jobDetail Job明细
     */
    protected void callBackOnCreatedJob(final JobDetail jobDetail) {
    }
    
    /**
     * 创建完Job触发器后的回调操作，可重写该方法设置Trigger参数。
     * 
     * @param trigger 触发器
     */
    protected void callBackOnCreatedTrigger(final Trigger trigger) {
    }
}
