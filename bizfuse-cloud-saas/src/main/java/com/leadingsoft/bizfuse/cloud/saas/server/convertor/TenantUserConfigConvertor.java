package com.leadingsoft.bizfuse.cloud.saas.server.convertor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.common.web.dto.AbstractConvertor;
import com.leadingsoft.bizfuse.cloud.saas.dto.TenantUserConfigDTO;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantUserConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.service.TenantUserConfigService;

import lombok.NonNull;

/**
 * TenantUserConfigConvertor
 */
@Component
public class TenantUserConfigConvertor extends AbstractConvertor<TenantUserConfig, TenantUserConfigDTO> {

    @Autowired
    private TenantUserConfigService tenantUserConfigService;
    
    @Override
    public TenantUserConfig toModel(@NonNull final TenantUserConfigDTO dto) {
        if (dto.isNew()) {//新增
            return constructModel(dto);
        } else {//更新
            return updateModel(dto);
        }
    }

    @Override
    public TenantUserConfigDTO toDTO(@NonNull final TenantUserConfig model, final boolean forListView) {
        final TenantUserConfigDTO dto = new TenantUserConfigDTO();
        dto.setId(model.getId());
        dto.setTenantNo(model.getTenantNo());
        dto.setUserNo(model.getUserNo());

        return dto;
    }

    // 构建新Model
    private TenantUserConfig constructModel(final TenantUserConfigDTO dto) {
        TenantUserConfig model = new TenantUserConfig();
        model.setTenantNo(dto.getTenantNo());
        model.setUserNo(dto.getUserNo());

        return model;
    }

    // 更新Model
    private TenantUserConfig updateModel(final TenantUserConfigDTO dto) {
        TenantUserConfig model = tenantUserConfigService.get(dto.getId());
        model.setTenantNo(dto.getTenantNo());
        model.setUserNo(dto.getUserNo());

        return model;
    }
}
