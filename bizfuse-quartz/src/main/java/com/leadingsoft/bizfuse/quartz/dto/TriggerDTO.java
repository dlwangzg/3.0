package com.leadingsoft.bizfuse.quartz.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class TriggerDTO {

    private String triggerKey;
    @NotNull
    private String triggerGroup;
    @NotNull
    private String triggerName;

    private String jobKey;

    private String description;

    private int priority;

    private boolean mayFireAgain;

    private Date startTime;

    private Date endTime;

    private Date nextFireTime;

    private Date previousFireTime;

    private Date finalFireTime;

    private String triggerState;

    private String triggerType;

    private String cronExpression;

    private long repeatInterval;

    private String jobGroup;

    private String jobName;

    public String getTriggerKey() {
        return this.triggerKey;
    }

    public void setTriggerKey(final String triggerKey) {
        this.triggerKey = triggerKey;
    }

    public String getTriggerGroup() {
        return this.triggerGroup;
    }

    public void setTriggerGroup(final String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    public String getTriggerName() {
        return this.triggerName;
    }

    public void setTriggerName(final String triggerName) {
        this.triggerName = triggerName;
    }

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

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(final int priority) {
        this.priority = priority;
    }

    public boolean isMayFireAgain() {
        return this.mayFireAgain;
    }

    public void setMayFireAgain(final boolean mayFireAgain) {
        this.mayFireAgain = mayFireAgain;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }

    public Date getNextFireTime() {
        return this.nextFireTime;
    }

    public void setNextFireTime(final Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public Date getPreviousFireTime() {
        return this.previousFireTime;
    }

    public void setPreviousFireTime(final Date previousFireTime) {
        this.previousFireTime = previousFireTime;
    }

    public Date getFinalFireTime() {
        return this.finalFireTime;
    }

    public void setFinalFireTime(final Date finalFireTime) {
        this.finalFireTime = finalFireTime;
    }

    public String getTriggerState() {
        return this.triggerState;
    }

    public void setTriggerState(final String triggerState) {
        this.triggerState = triggerState;
    }

    public String getCronExpression() {
        return this.cronExpression;
    }

    public void setCronExpression(final String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getTriggerType() {
        return this.triggerType;
    }

    public void setTriggerType(final String triggerType) {
        this.triggerType = triggerType;
    }

    public long getRepeatInterval() {
        return this.repeatInterval;
    }

    public void setRepeatInterval(final long repeatInterval) {
        this.repeatInterval = repeatInterval;
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

}
