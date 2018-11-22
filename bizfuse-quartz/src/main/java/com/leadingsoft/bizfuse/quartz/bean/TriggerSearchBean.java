package com.leadingsoft.bizfuse.quartz.bean;

public class TriggerSearchBean {

    private String jobName;
    private String triggerName;
    private String triggerState;

    public String getJobName() {
        return this.jobName;
    }

    public void setJobName(final String jobId) {
        this.jobName = jobId;
    }

    public String getTriggerName() {
        return this.triggerName;
    }

    public void setTriggerName(final String triggerName) {
        this.triggerName = triggerName;
    }

    public String getTriggerState() {
        return this.triggerState;
    }

    public void setTriggerState(final String triggerState) {
        this.triggerState = triggerState;
    }
}
