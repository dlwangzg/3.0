package com.leadingsoft.bizfuse.util.impl.redis;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.RedisTemplate;

import com.leadingsoft.bizfuse.util.Queue;
import com.leadingsoft.bizfuse.util.QueueManager;

/**
 * 队列存储管理的Redis实现
 *
 * @author liuyg
 */
public class QueueManagerRedisImpl implements QueueManager {

    private final RedisTemplate<String, Object> redisJsonTemplate;
    private final ConcurrentHashMap<String, Queue> queues = new ConcurrentHashMap<>();

    public QueueManagerRedisImpl(final RedisTemplate<String, Object> redisJsonTemplate) {
        this.redisJsonTemplate = redisJsonTemplate;
    }

    @Override
    public Queue getQueue(final String queueId) {
        Queue storage = this.queues.get(queueId);
        if (storage == null) {
            storage = new QueueRedisImpl(queueId, this.redisJsonTemplate.boundListOps(queueId));
            this.queues.putIfAbsent(queueId, storage);
        }
        return storage;
    }
}
