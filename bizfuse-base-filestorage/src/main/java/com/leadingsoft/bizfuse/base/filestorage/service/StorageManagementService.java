package com.leadingsoft.bizfuse.base.filestorage.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.leadingsoft.bizfuse.base.filestorage.dto.DownloadUrlDTO;
import com.leadingsoft.bizfuse.base.filestorage.dto.StorageRecordDTO;
import com.leadingsoft.bizfuse.base.filestorage.enums.NormalizationType;

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
    String getUploadUrl();

    /**
     * 获取文件下载URL
     *
     * @param id
     * @return
     */
    DownloadUrlDTO getDownloadUrl(Long id);

    /**
     * 对存储的文件执行标准化处理
     *
     * @param id 存储记录ID
     * @param sync 同步处理
     * @throws IOException
     */
    StorageRecordDTO normalize(Long id, boolean sync);

    /**
     * 合成多张图片为一张图片，最多支持九宫格样式（九张图合成）
     *
     * @param imagePath
     * @return
     */
    StorageRecordDTO createCombinationImage(List<String> imagePath, NormalizationType type);

    /**
     * 从后端存储下载文件到本地，支持数据文件标准化操作。
     *
     * @param id 存储记录ID
     * @param type 存储对象类型
     * @return 文件
     */
    File downloadToLocal(Long id, NormalizationType type);
}
