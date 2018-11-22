/**
 * 
 */
package com.leadingsoft.bizfuse.quartz.core.scheduler;

import java.util.Map;

import javax.annotation.Resource;

import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;

/**
 * 抽象的有时间限制的Job执行器类。
 * <p>
 * 初版，有待改进
 * </p>
 * 
 * @author liuyg
 * @version 0.1
 */
@Lazy(value = false)
public abstract class AbstractTimeLimitJobExecutor<T> implements TimeLimitJobExecutor<T> {
    
    //private static final Log LOGGER = LogFactory.getLog(AbstractTimeLimitJobExecutor.class);
    
    @Resource
    private ApplicationContext applicationContext;
    
    @Resource
    protected CommonJobsExecutor commonJobsExecutor;
    
    protected abstract boolean isJobFinished(T params);
    
    protected BusinessTask<T> businessTask;
    
    /**
     * 处理超时
     */
    protected abstract void handleTimeout();
    
    @Override
    public void startJob(final T params) {
        if (this.businessTask == null) {
            this.initBusinessTask();
        }
        final int calledTimes = 0;
        this.scheduleJobTask(params, calledTimes);
    }
    
    /*
     * (non-Javadoc)
     * @see
     * cn.com.dhc.common.job.TimeLimitJobExecutor#runJobTask(java.lang.Object,
     * int)
     */
    @Override
    public void runJobTask(final T params, int calledTimes) {
        try {
            if (this.businessTask == null) {
                this.initBusinessTask();
            }
            this.businessTask.runBusinessTask(params);
            if (!this.isJobFinished(params)) {
                this.scheduleJobTask(params, ++calledTimes);
            }
        } catch (final Throwable e) {
            // 处理失败 TODO:
            e.printStackTrace();
            // 同步异常，重新执行
            this.scheduleJobTask(params, calledTimes);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see cn.com.dhc.common.job.AbstractTimeLimitJobExecutor#getJobId()
     */
    @Override
    public String getJobKey() {
        return this.getClass().getName() + ".runBusinessTimeLimitTask";
    }
    
    /**
     * 执行内部的业务逻辑。
     * 
     * @param params
     * @param calledTimes
     */
    protected void runInnerTask(final T params, final int calledTimes) {
        
    }
    
    protected void scheduleJobTask(final T params, final int calledTimes) {
        if (this.isTaskTimeout(calledTimes)) {
            // 超时处理
            this.handleTimeout();
        } else { // 调起Job，执行异步任务
            try {
                this.commonJobsExecutor.scheduleJob(this.getJobKey(), this.getExcecutionDelaySeconds(), params,
                        calledTimes);
            } catch (final SchedulerException e) {
                throw new RuntimeException("Job schedule exception!", e);
            }
        }
    }
    
    /**
     * 校验超时。
     * 
     * @param calledTimes
     * @throws WorkOrderFailureException
     */
    protected boolean isTaskTimeout(final int calledTimes) {
        if (calledTimes >= this.getMaxRetryTimes()) {
            return true;
        } else {
            return false;
        }
    }
    
    @SuppressWarnings({"rawtypes", "unchecked" })
    private void initBusinessTask() {
        final Map<String, BusinessTask> beans = this.applicationContext.getBeansOfType(BusinessTask.class);
        for (final BusinessTask bean : beans.values()) {
            if ((bean.getJobKey() != null) && this.getJobKey().equals(bean.getJobKey())) {
                this.businessTask = bean;
                break;
            }
        }
    }
    
    /**
     * 如果要自定义最大执行次数，请在子类中重写该方法
     * 
     * @return
     */
    protected int getMaxRetryTimes() {
        return TimeLimitJobExecutor.MaxRetryTimes;
    }
    
    /**
     * 如果要自定义每次执行间隔，请在子类中重写该方法
     * 
     * @return
     */
    protected int getExcecutionDelaySeconds() {
        return TimeLimitJobExecutor.JobIntervalSec;
    }
}
