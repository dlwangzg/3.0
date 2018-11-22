package com.leadingsoft.bizfuse.base.filestorage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leadingsoft.bizfuse.base.filestorage.dto.DownloadUrlDTO;
import com.leadingsoft.bizfuse.base.filestorage.dto.UploadUrlDTO;
import com.leadingsoft.bizfuse.base.filestorage.service.StorageManagementService;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;

/**
 * 本地存储获取上传下载地址的接口
 *
 * @author shangxq
 */

@RestController
public class StorageManagementController {
    @Autowired
    StorageManagementService storageManagementService;

    /**
     * 取得上传地址
     *
     * @return 上传地址dto
     */
    @RequestMapping(value = "/uploadUrl", method = RequestMethod.GET)
    public ResultDTO<UploadUrlDTO> getUploadUrl(@RequestParam(defaultValue = "false") final boolean internalUrl) {
        final UploadUrlDTO dto = new UploadUrlDTO();
        dto.setUrl(this.storageManagementService.getUploadUrl(internalUrl));
        return ResultDTO.success(dto);
    }

    /**
     * 取得下载地址
     *
     * @param id 文件no||id
     * @return 下载地址dto
     */
    @RequestMapping(value = "/downloadUrl/{id}", method = RequestMethod.GET)
    public ResultDTO<DownloadUrlDTO> getDownloadUrl(@PathVariable final String id,
            @RequestParam(defaultValue = "false") final boolean internalUrl) {
        return ResultDTO.success(this.storageManagementService.getDownloadUrl(id, internalUrl));
    }

}
