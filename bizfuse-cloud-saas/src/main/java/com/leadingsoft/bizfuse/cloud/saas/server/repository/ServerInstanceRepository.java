package com.leadingsoft.bizfuse.cloud.saas.server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.cloud.saas.server.model.ServerInstance;

/**
 * ServerInstanceRepository
 */
public interface ServerInstanceRepository extends Repository<ServerInstance, Long>, ServerInstanceRepositoryCustom {

    Page<ServerInstance> findAll(Pageable pageable);

    List<ServerInstance> findAll();

    ServerInstance findOne(Long id);

    ServerInstance findByInternalIPAndPort(String ip, int port);

    ServerInstance save(ServerInstance model);

    void delete(Long id);

}
