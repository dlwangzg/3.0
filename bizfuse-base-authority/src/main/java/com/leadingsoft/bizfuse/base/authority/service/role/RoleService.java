package com.leadingsoft.bizfuse.base.authority.service.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.leadingsoft.bizfuse.base.authority.model.role.Role;

public interface RoleService {

	Page<Role> findAll(Pageable pageable);
	
	/**
     * 根据ID获取资源
     *
     * @param model
     * @return Id所指向的资源实例
     * @throws 当Id所指向的资源不存在时，抛CustomRuntimeException异常
     */
    Role getRole(Long id);

	Role findById(Long id);

	Role createRole(Role paramModel);

	Role updateRole(Role paramModel);

	void deleteById(Long id);
}
