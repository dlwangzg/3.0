package com.leadingsoft.bizfuse.cloud.saas.server.convertor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.cloud.saas.dto.TenantServerConfigDTO;
import com.leadingsoft.bizfuse.cloud.saas.server.model.ServerInstance;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantServerConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.ServerInstanceRepository;
import com.leadingsoft.bizfuse.cloud.saas.server.service.TenantServerConfigService;
import com.leadingsoft.bizfuse.common.web.dto.AbstractConvertor;

import lombok.NonNull;

/**
 * TenantServerConfigConvertor
 */
@Component
public class TenantServerConfigConvertor extends AbstractConvertor<TenantServerConfig, TenantServerConfigDTO> {

    @Autowired
    private TenantServerConfigService tenantServerConfigService;
    @Autowired
    private ServerInstanceRepository serverInstanceRepository;

    @Override
    public TenantServerConfig toModel(@NonNull final TenantServerConfigDTO dto) {
        if (dto.isNew()) {//新增
            return this.constructModel(dto);
        } else {//更新
            return this.updateModel(dto);
        }
    }

    @Override
    public TenantServerConfigDTO toDTO(@NonNull final TenantServerConfig model, final boolean forListView) {
        final TenantServerConfigDTO dto = new TenantServerConfigDTO();
        dto.setId(model.getId());
        dto.setTenantNo(model.getTenantNo());
        dto.setServerId(model.getServerInstance().getId());
        dto.setServerType(model.getServerInstance().getType());
        dto.setServerInternalIP(model.getServerInstance().getInternalIP());
        dto.setServerPublicIP(model.getServerInstance().getPublicIP());
        dto.setPort(model.getServerInstance().getPort());
        dto.setServerRemarks(model.getServerInstance().getRemarks());

        return dto;
    }

    // 构建新Model
    private TenantServerConfig constructModel(final TenantServerConfigDTO dto) {
        final TenantServerConfig model = new TenantServerConfig();
        model.setTenantNo(dto.getTenantNo());
        final ServerInstance serverInstance = this.serverInstanceRepository.findOne(dto.getServerId());
        model.setServerInstance(serverInstance);
        return model;
    }

    // 更新Model
    private TenantServerConfig updateModel(final TenantServerConfigDTO dto) {
        final TenantServerConfig model = this.tenantServerConfigService.get(dto.getId());
        model.setTenantNo(dto.getTenantNo());
        final ServerInstance serverInstance = this.serverInstanceRepository.findOne(dto.getServerId());
        model.setServerInstance(serverInstance);

        return model;
    }
}
