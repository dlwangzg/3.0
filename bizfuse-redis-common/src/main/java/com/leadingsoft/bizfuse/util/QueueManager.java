package com.leadingsoft.bizfuse.util;

/**
 * 队列管理器
 *
 * @author liuyg
 */
public interface QueueManager {

    /**
     * 根据队列ID获取队列实例
     *
     * @param queueId
     * @return
     */
    public Queue getQueue(final String queueId);
}
