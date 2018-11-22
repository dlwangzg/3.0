/**
 * 
 */
package com.leadingsoft.bizfuse.quartz.core.scheduler;

import java.util.EventObject;

/**
 * Job超时异常。
 * 
 * @author liuyg
 * @version 1.0
 */
public class TimeoutBusinessEvent extends EventObject {
    
    /** serialVersionUID */
    private static final long serialVersionUID = -6391920213525851406L;
    
    /**
     * @param source
     */
    public TimeoutBusinessEvent(final Object source) {
        super(source);
    }
}
