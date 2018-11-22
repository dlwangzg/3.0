package com.leadingsoft.bizfuse.quartz.dto;

public class JobDetailDTO {
    private String jobKey;

    private String jobGroup;

    private String jobName;

    private String description;

    private boolean isDurable;

    private int triggerNum;

    private boolean paused;

    public String getJobKey() {
        return this.jobKey;
    }

    public void setJobKey(final String jobKey) {
        this.jobKey = jobKey;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isDurable() {
        return this.isDurable;
    }

    public void setDurable(final boolean isDurable) {
        this.isDurable = isDurable;
    }

    public int getTriggerNum() {
        return this.triggerNum;
    }

    public void setTriggerNum(final int triggerNum) {
        this.triggerNum = triggerNum;
    }

    public String getJobGroup() {
        return this.jobGroup;
    }

    public void setJobGroup(final String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobName() {
        return this.jobName;
    }

    public void setJobName(final String jobName) {
        this.jobName = jobName;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setPaused(final boolean paused) {
        this.paused = paused;
    }

}
