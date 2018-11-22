package com.leadingsoft.bizfuse.util.impl.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.BoundListOperations;

import com.leadingsoft.bizfuse.util.Queue;

/**
 * 队列存储的Redis实现
 *
 * @author liuyg
 */
public class QueueRedisImpl implements Queue {

    private final String queueId;

    private final BoundListOperations<String, Object> listOperations;

    public QueueRedisImpl(final String queueId, final BoundListOperations<String, Object> listOperations) {
        this.queueId = queueId;
        this.listOperations = listOperations;
    }

    @Override
    public String getQueueId() {
        return this.queueId;
    }

    @Override
    public void add(final Object element) {
        this.listOperations.rightPush(element);
    }

    @Override
    public Object peek() {
        return this.listOperations.index(0);
    }

    @Override
    public Object pop() {
        return this.listOperations.leftPop();
    }

    @Override
    public Long size() {
        return this.listOperations.size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public Object bPop(final long timeout, final TimeUnit unit) {
        return this.listOperations.leftPop(timeout, unit);
    }
}
