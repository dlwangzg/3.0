package com.leadingsoft.bizfuse.util.message;

import com.leadingsoft.bizfuse.util.Queue;

/**
 * 消息的主题
 *
 * @author liuyg
 */
public interface Topic {

    /**
     * 绑定主题
     *
     * @param key
     * @return 消息队列
     */
    Queue bind(String key);

    /**
     * 取消绑定（会删除已绑定的队列及数据，慎行！）
     *
     * @param key
     */
    void unbind(String key);
}
