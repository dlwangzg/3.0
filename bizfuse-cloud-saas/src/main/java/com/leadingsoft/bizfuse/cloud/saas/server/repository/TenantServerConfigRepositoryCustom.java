package com.leadingsoft.bizfuse.cloud.saas.server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.leadingsoft.bizfuse.cloud.saas.dto.TenantServerRelationBean;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantServerConfig;
import com.leadingsoft.bizfuse.common.web.support.Searchable;

public interface TenantServerConfigRepositoryCustom {

    Page<TenantServerConfig> page(Pageable pageable, Searchable searchable);

    List<TenantServerConfig> list(Searchable searchable);

    List<TenantServerRelationBean> findAll();
}
