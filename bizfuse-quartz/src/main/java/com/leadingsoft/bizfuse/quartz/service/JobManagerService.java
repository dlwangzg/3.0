package com.leadingsoft.bizfuse.quartz.service;

import org.quartz.JobKey;
import org.quartz.Trigger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.leadingsoft.bizfuse.quartz.bean.TriggerBean;
import com.leadingsoft.bizfuse.quartz.bean.TriggerSearchBean;

/**
 * job任务管理
 *
 * @author liuyg
 * @version 1.0
 */
public interface JobManagerService {

    /**
     * 查看job信息列表
     *
     * @param pagingDTO
     * @return
     */
    public Page<JobKey> getJobs(String search, Pageable pageable);

    /**
     * 暂停job任务
     *
     * @param jobKey
     * @return
     */
    public boolean pauseJob(final String jobKey);

    /**
     * 恢复job任务
     *
     * @param jobKey
     * @return
     */
    public boolean resumeJob(final String jobKey);

    /**
     * 删除job
     *
     * @param jobKey
     * @return
     */
    public boolean deleteJob(final String jobKey);

    /**
     * 查看对应jobkey的tragger列表
     *
     * @param search
     * @param pageable
     * @return
     */
    public Page<Trigger> getTriggers(TriggerSearchBean search, Pageable pageable);

    /**
     * 暂停tragger任务
     *
     * @param triggerKey
     * @return
     */
    public boolean pauseTrigger(final String triggerKey);

    /**
     * 恢复tragger任务
     *
     * @param triggerKey
     * @return
     */
    public boolean resumeTrigger(final String triggerKey);

    /**
     * 更改时间规则
     *
     * @param triggerBean
     * @return
     */
    public boolean updateTrigger(final TriggerBean triggerBean);

    /**
     * 增加立即触发的触发器
     *
     * @param triggerBean
     * @return
     */
    public boolean addTrigger(final TriggerBean triggerBean, final Object... params);

    /**
     * 删除trigger
     *
     * @param triggerKey
     * @return
     */
    public boolean deleteTrigger(final String triggerKey);
}
