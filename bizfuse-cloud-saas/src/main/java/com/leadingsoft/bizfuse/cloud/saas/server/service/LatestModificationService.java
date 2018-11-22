package com.leadingsoft.bizfuse.cloud.saas.server.service;

import com.leadingsoft.bizfuse.cloud.saas.server.model.LatestModification;

public interface LatestModificationService {

    /**
     * 更新最新变更时间
     *
     * @param model
     */
    void update(String model);

    /**
     * 获取指定时间之后的记录
     *
     * @param model
     * @param lastModifiedTime
     * @return
     */
    LatestModification getByModifiedTimeAfter(String model, long time);
}
