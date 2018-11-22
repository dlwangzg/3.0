/**
 * 
 */
package com.leadingsoft.bizfuse.quartz.core.managment;

/**
 * @author liuyg
 */
public interface JobConcurrentInfoHolder {
    
    boolean isAllowConcurrentExecute(String jobKey);
}
