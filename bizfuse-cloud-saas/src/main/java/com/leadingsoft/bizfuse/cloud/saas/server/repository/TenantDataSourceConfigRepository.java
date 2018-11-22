package com.leadingsoft.bizfuse.cloud.saas.server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantDataSourceConfig;

/**
 * TenantDataSourceConfigRepository
 */
public interface TenantDataSourceConfigRepository
        extends Repository<TenantDataSourceConfig, Long>, TenantDataSourceConfigRepositoryCustom {

    Page<TenantDataSourceConfig> findAll(Pageable pageable);

    List<TenantDataSourceConfig> findByServerTypeAndTenantNoIn(String serverType, List<String> tenantNos);

    TenantDataSourceConfig findOne(Long id);

    TenantDataSourceConfig save(TenantDataSourceConfig model);

    void delete(Long id);

    List<TenantDataSourceConfig> findByTenantNo(String tenantNo);

    List<TenantDataSourceConfig> findByOrderByTenantNoAsc();

}
