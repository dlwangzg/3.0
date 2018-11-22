package com.leadingsoft.bizfuse.cloud.saas.server.convertor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.common.web.dto.AbstractConvertor;
import com.leadingsoft.bizfuse.cloud.saas.dto.TenantDataSourceConfigDTO;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantDataSourceConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.service.TenantDataSourceConfigService;

import lombok.NonNull;

/**
 * TenantDataSourceConfigConvertor
 */
@Component
public class TenantDataSourceConfigConvertor extends AbstractConvertor<TenantDataSourceConfig, TenantDataSourceConfigDTO> {

    @Autowired
    private TenantDataSourceConfigService tenantDataSourceConfigService;
    
    @Override
    public TenantDataSourceConfig toModel(@NonNull final TenantDataSourceConfigDTO dto) {
        if (dto.isNew()) {//新增
            return constructModel(dto);
        } else {//更新
            return updateModel(dto);
        }
    }

    @Override
    public TenantDataSourceConfigDTO toDTO(@NonNull final TenantDataSourceConfig model, final boolean forListView) {
        final TenantDataSourceConfigDTO dto = new TenantDataSourceConfigDTO();
        dto.setId(model.getId());
        dto.setTenantNo(model.getTenantNo());
        dto.setServerType(model.getServerType());
        dto.setDriverClassName(model.getDriverClassName());
        dto.setUrl(model.getUrl());
        dto.setUsername(model.getUsername());
        dto.setPassword(model.getPassword());

        return dto;
    }

    // 构建新Model
    private TenantDataSourceConfig constructModel(final TenantDataSourceConfigDTO dto) {
        TenantDataSourceConfig model = new TenantDataSourceConfig();
        model.setTenantNo(dto.getTenantNo());
        model.setServerType(dto.getServerType());
        model.setDriverClassName(dto.getDriverClassName());
        model.setUrl(dto.getUrl());
        model.setUsername(dto.getUsername());
        model.setPassword(dto.getPassword());

        return model;
    }

    // 更新Model
    private TenantDataSourceConfig updateModel(final TenantDataSourceConfigDTO dto) {
        TenantDataSourceConfig model = tenantDataSourceConfigService.get(dto.getId());
        model.setTenantNo(dto.getTenantNo());
        model.setServerType(dto.getServerType());
        model.setDriverClassName(dto.getDriverClassName());
        model.setUrl(dto.getUrl());
        model.setUsername(dto.getUsername());
        model.setPassword(dto.getPassword());

        return model;
    }
}
