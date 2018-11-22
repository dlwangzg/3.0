package com.leadingsoft.bizfuse.base.filestorage.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leadingsoft.bizfuse.base.filestorage.model.StorageRecord;
import com.leadingsoft.bizfuse.base.filestorage.repository.StorageRecordRepository;
import com.leadingsoft.bizfuse.base.filestorage.service.StorageRecordService;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

import lombok.NonNull;

/**
 * StorageRecordService 实现类
 */
@Service
@Transactional
public class StorageRecordServiceImpl implements StorageRecordService {

    private final Pattern numberPattern = Pattern.compile("\\d+");
    @Autowired
    private StorageRecordRepository storageRecordRepository;

    @Override
    public StorageRecord getStorageRecord(@NonNull final Long id) {
        final StorageRecord model = this.storageRecordRepository.findOne(id);
        if (model == null) {
            throw new CustomRuntimeException("404", String.format("查找的资源[%s]不存在.", id));
        }
        return model;
    }

    @Override
    public StorageRecord getStorageRecordByNo(@NonNull final String no) {
        StorageRecord model = null;
        if (this.numberPattern.matcher(no).matches()) {
            model = this.storageRecordRepository.findOne(Long.parseLong(no));
        } else {
            model = this.storageRecordRepository.findOneByNo(no);
        }
        if (model == null) {
            throw new CustomRuntimeException("404", String.format("查找的资源[%s]不存在.", no));
        }
        return model;
    }

    @Override
    public StorageRecord createStorageRecord(final StorageRecord model) {
        return this.storageRecordRepository.save(model);
    }

    @Override
    public StorageRecord updateStorageRecord(final StorageRecord model) {
        return this.storageRecordRepository.save(model);
    }

    @Override
    public void deleteStorageRecord(@NonNull final Long id) {
        this.storageRecordRepository.delete(id);
    }

    @Override
    public List<StorageRecord> getStorageRecords(final List<String> storageNos) {
        if (storageNos.isEmpty()) {
            return Collections.emptyList();
        }
        if (this.numberPattern.matcher(storageNos.get(0)).matches()) {
            final List<Long> nos = storageNos.stream().map(no -> {
                return Long.parseLong(no);
            }).collect(Collectors.toList());
            return this.storageRecordRepository.findAllByIdIn(nos);
        } else {
            return this.storageRecordRepository.findAllByNoIn(storageNos);
        }
    }
}
