package com.leadingsoft.bizfuse.base.authority.repository.role;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.base.authority.model.role.Role;

public interface RoleRepository extends Repository<Role, Long> {

    Page<Role> findAll(final Pageable pageable);

    List<Role> findAll();

    List<Role> findByNameContaining(String name);

    Role findOne(final Long id);

    Role findOneByName(final String name);

    Role save(final Role platformRole);

    void delete(final Long id);

}
