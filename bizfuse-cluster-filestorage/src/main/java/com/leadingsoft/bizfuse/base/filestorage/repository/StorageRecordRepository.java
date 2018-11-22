package com.leadingsoft.bizfuse.base.filestorage.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.base.filestorage.model.StorageRecord;

/**
 * StorageRecordRepository
 */
public interface StorageRecordRepository extends Repository<StorageRecord, String> {

    Page<StorageRecord> findAll(Pageable pageable);

    StorageRecord findOne(String id);

    StorageRecord save(StorageRecord model);

    void delete(String id);

    List<StorageRecord> findAllByIdIn(List<String> storageIds);

}
