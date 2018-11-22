/**
 * 
 */
package com.leadingsoft.bizfuse.quartz.core.scheduler;

/**
 * @author liuyg
 */
public interface BusinessTask<T> {
    /**
     * 执行业务逻辑
     * 
     * @param params
     */
    void runBusinessTask(T params);
    
    String getJobKey();
}
