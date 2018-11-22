package com.leadingsoft.bizfuse.quartz.bean;

import java.util.Date;

import javax.validation.constraints.NotNull;

public class TriggerBean {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////// 可配置参数
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 触发器类型（新建时可编辑）
     */
    @NotNull
    private TriggerType triggerType;
    /**
     * JOB的主键（新建时可编辑）
     */
    @NotNull
    private String jobKey;

    /**
     * 触发器主键，不可重复（新建时可编辑）
     */
    @NotNull
    private String triggerKey;

    /**
     * 触发器描述（新建、更新时可编辑）
     */
    private String description;

    /**
     * 优先级，默认0 （新建、更新时可编辑）
     */
    private int priority;
    /**
     * 触发器生效时间（新建、更新时可编辑）
     */
    private Date startTime;
    /**
     * 触发器终止时间（新建、更新时可编辑）
     */
    private Date endTime;
    /**
     * 触发规则（新建、更新时可编辑）
     */
    private String cronExpression;
    /**
     * 重复间隔（新建、更新时可编辑）
     */
    private long repeatInterval;
    /**
     * 重复次数（新建、更新时可编辑）
     */
    private int repeatCount;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////// 运行时属性，不可配置参数
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Job描述（不可编辑）
     */
    private String jobDescription;
    /**
     * 能再次触发（不可编辑）
     */
    private boolean mayFireAgain;
    /**
     * 下次触发时间（不可编辑）
     */
    private Date nextFireTime;
    /**
     * 上次触发时间（不可编辑）
     */
    private Date previousFireTime;
    /**
     * 最终触发时间（不可编辑）
     */
    private Date finalFireTime;
    /**
     * 触发器状态（不可编辑）
     */
    private String triggerState;

    public String getTriggerKey() {
        return this.triggerKey;
    }

    public void setTriggerKey(final String triggerKey) {
        this.triggerKey = triggerKey;
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

    public TriggerType getTriggerType() {
        return this.triggerType;
    }

    public void setTriggerType(final TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public long getRepeatInterval() {
        return this.repeatInterval;
    }

    public void setRepeatInterval(final long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public int getRepeatCount() {
        return this.repeatCount;
    }

    public void setRepeatCount(final int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public String getJobDescription() {
        return this.jobDescription;
    }

    public void setJobDescription(final String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public enum TriggerType {
        SimpleTrigger, CronTrigger
    }
}
