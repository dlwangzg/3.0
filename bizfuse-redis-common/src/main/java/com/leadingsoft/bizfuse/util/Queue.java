package com.leadingsoft.bizfuse.util;

import java.util.concurrent.TimeUnit;

public interface Queue {

    /**
     * 队列的主键
     *
     * @return
     */
    String getQueueId();

    /**
     * 添加元素到队列
     *
     * @param element
     */
    void add(Object element);

    /**
     * 获取但不删除队头的元素
     *
     * @return
     */
    Object peek();

    /**
     * 获取并删除队头的元素
     *
     * @return
     */
    Object pop();

    /**
     * 获取并删除队头的元素
     *
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return
     */
    Object bPop(long timeout, TimeUnit unit);

    /**
     * 队列长度
     *
     * @return
     */
    Long size();

    /**
     * 是否为空队列
     *
     * @return
     */
    boolean isEmpty();
}
