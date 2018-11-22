package com.leadingsoft.bizfuse.base.filestorage.repository;


import com.leadingsoft.bizfuse.base.filestorage.model.StorageServerInstance;
import org.springframework.data.repository.Repository;

import java.util.List;

/**
 * StorageServerInstanceRepository
 */
public interface StorageServerInstanceRepository extends Repository<StorageServerInstance,String> {
    List<StorageServerInstance> findAll();

    StorageServerInstance save(StorageServerInstance model);

    void delete(String id);

    StorageServerInstance findOne(String id);

    StorageServerInstance findOneByInternalIPAndInternalPort(String ip,String port);
}
