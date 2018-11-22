package com.leadingsoft.bizfuse.base.filestorage.service;

import com.leadingsoft.bizfuse.base.filestorage.dto.DownloadUrlDTO;
import com.leadingsoft.bizfuse.base.filestorage.model.StorageRecord;

/**
 * 存储管理服务接口
 *
 * @author liuyg
 */
public interface StorageManagementService {

    /**
     * 获取文件上传URL
     *
     * @return
     */
    String getUploadUrl(boolean internalUrl);

    /**
     * 获取文件下载URL
     *
     * @param id
     * @return
     */
    DownloadUrlDTO getDownloadUrl(String id, boolean internalUrl);

    /**
     * 下载重定向
     *
     * @param record
     * @return
     */
    String getRedirectUrlIfNeed(StorageRecord record);

}
