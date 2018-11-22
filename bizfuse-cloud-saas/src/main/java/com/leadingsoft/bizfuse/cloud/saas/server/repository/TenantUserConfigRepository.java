package com.leadingsoft.bizfuse.cloud.saas.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantUserConfig;

/**
 * TenantUserConfigRepository
 */
public interface TenantUserConfigRepository extends Repository<TenantUserConfig, Long> {

    Page<TenantUserConfig> findAll(Pageable pageable);

    TenantUserConfig findOne(Long id);

    TenantUserConfig findByUserNo(String userNo);

    TenantUserConfig save(TenantUserConfig model);

    void delete(Long id);

}
