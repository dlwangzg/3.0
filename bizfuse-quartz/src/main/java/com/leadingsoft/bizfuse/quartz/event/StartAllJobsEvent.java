package com.leadingsoft.bizfuse.quartz.event;

import java.util.Date;

import org.springframework.context.ApplicationEvent;

public class StartAllJobsEvent extends ApplicationEvent {

    private static final long serialVersionUID = -1827436057375801468L;

    public StartAllJobsEvent() {
        super(new Date());
    }

}
