package com.leadingsoft.bizfuse.base.filestorage.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.base.filestorage.model.StorageRecord;

/**
 * StorageRecordRepository
 */
public interface StorageRecordRepository extends Repository<StorageRecord, Long> {

    Page<StorageRecord> findAll(Pageable pageable);

    StorageRecord findOne(Long id);

    StorageRecord save(StorageRecord model);

    void delete(Long id);

    List<StorageRecord> findAllByIdIn(List<Long> storageIds);

    StorageRecord findOneByNo(String no);

    List<StorageRecord> findAllByNoIn(List<String> storageNos);

}
