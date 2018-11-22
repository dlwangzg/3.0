package com.leadingsoft.bizfuse.cloud.saas.server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.cloud.saas.server.model.ServerInstance;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantServerConfig;

/**
 * TenantServerConfigRepository
 */
public interface TenantServerConfigRepository
        extends Repository<TenantServerConfig, Long>, TenantServerConfigRepositoryCustom {

    Page<TenantServerConfig> findAll(Pageable pageable);

    TenantServerConfig findOne(Long id);

    TenantServerConfig save(TenantServerConfig model);

    void delete(Long id);

    TenantServerConfig findByTenantNoAndServerInstance(String tenantNo, ServerInstance serverInstance);

    List<TenantServerConfig> findByTenantNo(String tenantNo);

    List<TenantServerConfig> findByOrderByTenantNoAsc();

}
