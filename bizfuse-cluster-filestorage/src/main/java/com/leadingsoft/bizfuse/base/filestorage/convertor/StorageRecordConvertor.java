package com.leadingsoft.bizfuse.base.filestorage.convertor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.base.filestorage.dto.StorageRecordDTO;
import com.leadingsoft.bizfuse.base.filestorage.model.StorageRecord;
import com.leadingsoft.bizfuse.base.filestorage.service.StorageRecordService;
import com.leadingsoft.bizfuse.common.web.dto.AbstractConvertor;

/**
 * StorageRecordConvertor
 */
@Component
public class StorageRecordConvertor extends AbstractConvertor<StorageRecord, StorageRecordDTO> {

    @Autowired
    private StorageRecordService storageRecordService;

    @Override
    public StorageRecord toModel(final StorageRecordDTO dto) {
        if (dto.isNew()) {//新增
            return this.constructModel(dto);
        } else {//更新
            return this.updateModel(dto);
        }
    }

    @Override
    public StorageRecordDTO toDTO(final StorageRecord model, final boolean forListView) {
        if (model == null) {
            return null;
        }
        final StorageRecordDTO dto = new StorageRecordDTO();
        dto.setId(model.getId());
        dto.setObjectType(model.getObjectType());
        dto.setFilePath(model.getFilePath());
        dto.setFileName(model.getFileName());
        dto.setFileSize(model.getFileSize());
        dto.setDuration(model.getDuration());
        dto.setCreatedDate(model.getCreatedDate());
        return dto;
    }

    // 构建新Model
    private StorageRecord constructModel(final StorageRecordDTO dto) {
        final StorageRecord model = new StorageRecord();
        model.setObjectType(dto.getObjectType());
        model.setFilePath(dto.getFilePath());
        model.setFileName(dto.getFileName());
        model.setFileSize(dto.getFileSize());
        model.setDuration(dto.getDuration());

        return model;
    }

    // 更新Model
    private StorageRecord updateModel(final StorageRecordDTO dto) {
        final StorageRecord model = this.storageRecordService.getStorageRecord(dto.getId());
        model.setObjectType(dto.getObjectType());
        model.setFilePath(dto.getFilePath());
        model.setFileName(dto.getFileName());
        model.setFileSize(dto.getFileSize());
        model.setDuration(dto.getDuration());

        return model;
    }
}
