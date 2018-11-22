package com.leadingsoft.bizfuse.base.authority.repository.authorization;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.base.authority.model.authorization.RoleMenusAuthorization;
import com.leadingsoft.bizfuse.base.authority.model.role.Role;

public interface RoleMenusAuthorizationRepository extends Repository<RoleMenusAuthorization, Long> {

    public RoleMenusAuthorization save(final RoleMenusAuthorization systemAuthorization);

    public RoleMenusAuthorization findOneByRoleId(final Long roleId);

    public List<RoleMenusAuthorization> findAllByRoleIn(final Collection<Role> roles);

    public void deleteByRoleId(final Long roleId);

    public List<RoleMenusAuthorization> findAll();
}
