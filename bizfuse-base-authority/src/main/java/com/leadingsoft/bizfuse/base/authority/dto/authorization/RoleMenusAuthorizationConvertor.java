package com.leadingsoft.bizfuse.base.authority.dto.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.base.authority.model.authorization.RoleMenusAuthorization;
import com.leadingsoft.bizfuse.base.authority.model.role.Role;
import com.leadingsoft.bizfuse.base.authority.repository.authorization.RoleMenusAuthorizationRepository;
import com.leadingsoft.bizfuse.base.authority.service.role.RoleService;
import com.leadingsoft.bizfuse.common.web.dto.AbstractConvertor;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;

@Component
public class RoleMenusAuthorizationConvertor
        extends AbstractConvertor<RoleMenusAuthorization, RoleMenusAuthorizationDTO> {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleMenusAuthorizationRepository systemAuthorizationRepository;

    @Override
    public RoleMenusAuthorization toModel(final RoleMenusAuthorizationDTO dto) {
        final Role role = this.roleService.findById(dto.getId());
        RoleMenusAuthorization model = this.systemAuthorizationRepository.findOneByRoleId(dto.getId());
        if (model == null) {
            model = new RoleMenusAuthorization();
            model.setRole(role);
        }
        final long[] menuIds = dto.getMenuIds();
        model.setAuthorizeMenus(JsonUtils.pojoToJson(menuIds));
        return model;
    }

    @Override
    public RoleMenusAuthorizationDTO toDTO(final RoleMenusAuthorization model, final boolean forListView) {
        final RoleMenusAuthorizationDTO systemAuthorizationDTO = new RoleMenusAuthorizationDTO();
        systemAuthorizationDTO.setId(model.getRole().getId());
        systemAuthorizationDTO.setMenuIds(JsonUtils.jsonToPojo(model.getAuthorizeMenus(), long[].class));
        return systemAuthorizationDTO;
    }

}
