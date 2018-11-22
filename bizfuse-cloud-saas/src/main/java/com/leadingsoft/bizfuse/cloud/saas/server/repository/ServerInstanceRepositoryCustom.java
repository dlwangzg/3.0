package com.leadingsoft.bizfuse.cloud.saas.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.leadingsoft.bizfuse.cloud.saas.dto.ServerUsageBean;
import com.leadingsoft.bizfuse.cloud.saas.server.model.ServerInstance;
import com.leadingsoft.bizfuse.common.web.support.Searchable;

public interface ServerInstanceRepositoryCustom {

    Page<ServerInstance> search(Pageable pageable, Searchable searchable);

    Page<ServerUsageBean> searchUsage(Pageable pageable, Searchable searchable);
}
