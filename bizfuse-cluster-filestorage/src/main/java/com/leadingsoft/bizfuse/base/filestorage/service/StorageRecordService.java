package com.leadingsoft.bizfuse.base.filestorage.service;

import java.util.List;

import com.leadingsoft.bizfuse.base.filestorage.model.StorageRecord;

/**
 * StorageRecordService
 */
public interface StorageRecordService {

    /**
     * 根据ID获取资源
     *
     * @param id
     * @return Id所指向的资源实例
     * @throw 当id所指向的资源不存在时，抛CustomRuntimeException异常
     */
    StorageRecord getStorageRecord(String id);

    /**
     * 创建
     *
     * @param model
     * @return 创建后的对象
     */
    StorageRecord createStorageRecord(StorageRecord model);

    /**
     * 更新
     *
     * @param model
     * @return 修改后的对象
     */
    StorageRecord updateStorageRecord(StorageRecord model);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    void deleteStorageRecord(String id);

    /**
     * 获取存储记录列表
     *
     * @param storageIds
     */
    List<StorageRecord> getStorageRecords(List<String> storageIds);

    String getInternalIP();

    String getPort();
}
