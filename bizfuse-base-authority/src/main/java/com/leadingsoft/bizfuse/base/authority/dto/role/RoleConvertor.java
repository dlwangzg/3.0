package com.leadingsoft.bizfuse.base.authority.dto.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.base.authority.model.role.Role;
import com.leadingsoft.bizfuse.base.authority.service.role.RoleService;
import com.leadingsoft.bizfuse.common.web.dto.AbstractConvertor;

@Component
public class RoleConvertor extends AbstractConvertor<Role, RoleDTO> {

    @Autowired
    private RoleService roleService;

    @Override
    public Role toModel(final RoleDTO dto) {
        if (dto.isNew()) {//新增
            return this.constructModel(dto);
        } else {//更新
            return this.updateModel(dto);
        }
    }

    @Override
    public RoleDTO toDTO(final Role model, final boolean forListView) {
        if (model == null) {
            return null;
        }
        final RoleDTO dto = new RoleDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setDescription(model.getDescription());

        return dto;
    }

    // 构建新Model
    private Role constructModel(final RoleDTO dto) {
        final Role model = new Role();
        model.setName(dto.getName());
        model.setDescription(dto.getDescription());

        return model;
    }

    // 更新Model
    private Role updateModel(final RoleDTO dto) {
        final Role model = this.roleService.getRole(dto.getId());
        //model.setName(dto.getName());
        model.setDescription(dto.getDescription());

        return model;
    }
}
