package com.leadingsoft.bizfuse.base.authority.service.role.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leadingsoft.bizfuse.base.authority.model.role.Role;
import com.leadingsoft.bizfuse.base.authority.repository.role.RoleRepository;
import com.leadingsoft.bizfuse.base.authority.service.authorization.SystemAuthorizationService;
import com.leadingsoft.bizfuse.base.authority.service.role.RoleService;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

import lombok.NonNull;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private SystemAuthorizationService systemAuthorizationService;

    @Override
    @Transactional(readOnly = true)
    public Page<Role> findAll(final Pageable pageable) {
        return this.roleRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRole(@NonNull final Long id) {
        final Role model = this.roleRepository.findOne(id);
        if (model == null) {
            throw new CustomRuntimeException("404", String.format("查找的资源[%s]不存在.", id));
        }
        return model;
    }

    @Override
    @Transactional(readOnly = true)
    public Role findById(final Long id) {
        return this.roleRepository.findOne(id);
    }

    @Override
    @Transactional
    public Role createRole(final Role paramModel) {
        return this.roleRepository.save(paramModel);
    }

    @Override
    @Transactional
    public Role updateRole(final Role paramModel) {
        return this.roleRepository.save(paramModel);
    }

    @Override
    @Transactional
    public void deleteById(final Long id) {
        final Role role = this.roleRepository.findOne(id);
        if (role == null) {
            return;
        }
        // 删除角色的菜单授权
        this.systemAuthorizationService.deleteMenusAuthorization(id);
        // 收回用户已分配的角色
        this.systemAuthorizationService.revokeUsersRole(role);
        // 删除角色
        this.roleRepository.delete(id);
    }

}
