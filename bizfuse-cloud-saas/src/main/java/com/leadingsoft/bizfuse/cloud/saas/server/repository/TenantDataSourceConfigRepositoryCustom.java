package com.leadingsoft.bizfuse.cloud.saas.server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.leadingsoft.bizfuse.cloud.saas.dto.TenantDataSourceSyncBean;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantDataSourceConfig;
import com.leadingsoft.bizfuse.common.web.support.Searchable;

public interface TenantDataSourceConfigRepositoryCustom {

    Page<TenantDataSourceConfig> page(Pageable pageable, Searchable searchable);

    List<TenantDataSourceConfig> search(TenantDataSourceSyncBean search);
}
