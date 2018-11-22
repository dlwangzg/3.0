package com.leadingsoft.bizfuse.base.authority.repository.authorization;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.base.authority.model.authorization.UserGrantedRole;
import com.leadingsoft.bizfuse.base.authority.model.role.Role;

public interface UserGrantedRoleRepository extends Repository<UserGrantedRole, Long> {

    List<UserGrantedRole> findAllByUserNo(String userNo);

    UserGrantedRole findOne(final Long id);

    UserGrantedRole save(final UserGrantedRole grantedRole);

    void delete(final Long id);

    void deleteByUserNoAndRoleId(String userNo, Long roleId);

    List<UserGrantedRole> findAll();

    void deleteByRole(Role role);
}
