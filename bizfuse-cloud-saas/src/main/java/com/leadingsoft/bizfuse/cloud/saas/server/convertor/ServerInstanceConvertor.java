package com.leadingsoft.bizfuse.cloud.saas.server.convertor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.common.web.dto.AbstractConvertor;
import com.leadingsoft.bizfuse.cloud.saas.dto.ServerInstanceDTO;
import com.leadingsoft.bizfuse.cloud.saas.server.model.ServerInstance;
import com.leadingsoft.bizfuse.cloud.saas.server.service.ServerInstanceService;

import lombok.NonNull;

/**
 * ServerInstanceConvertor
 */
@Component
public class ServerInstanceConvertor extends AbstractConvertor<ServerInstance, ServerInstanceDTO> {

    @Autowired
    private ServerInstanceService serverInstanceService;
    
    @Override
    public ServerInstance toModel(@NonNull final ServerInstanceDTO dto) {
        if (dto.isNew()) {//新增
            return constructModel(dto);
        } else {//更新
            return updateModel(dto);
        }
    }

    @Override
    public ServerInstanceDTO toDTO(@NonNull final ServerInstance model, final boolean forListView) {
        final ServerInstanceDTO dto = new ServerInstanceDTO();
        dto.setId(model.getId());
        dto.setType(model.getType());
        dto.setInternalIP(model.getInternalIP());
        dto.setPublicIP(model.getPublicIP());
        dto.setRemarks(model.getRemarks());
        dto.setPort(model.getPort());

        return dto;
    }

    // 构建新Model
    private ServerInstance constructModel(final ServerInstanceDTO dto) {
        ServerInstance model = new ServerInstance();
        model.setType(dto.getType());
        model.setInternalIP(dto.getInternalIP());
        model.setPublicIP(dto.getPublicIP());
        model.setRemarks(dto.getRemarks());
        model.setPort(dto.getPort());

        return model;
    }

    // 更新Model
    private ServerInstance updateModel(final ServerInstanceDTO dto) {
        ServerInstance model = serverInstanceService.get(dto.getId());
        model.setType(dto.getType());
        model.setInternalIP(dto.getInternalIP());
        model.setPublicIP(dto.getPublicIP());
        model.setRemarks(dto.getRemarks());
        model.setPort(dto.getPort());

        return model;
    }
}
