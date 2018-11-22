/**
 * 
 */
package com.leadingsoft.bizfuse.quartz.core.listeners;

/**
 * 定时触发器Job派发监听。
 * 
 * @author liuyg
 */
public interface CronTriggerScheduleListener {
    
    void registerCronTrigger(org.quartz.CronTrigger cronTrigger);
    
    void removeCronTrigger(org.quartz.CronTrigger cronTrigger);
}
