package com.leadingsoft.bizfuse.base.authority.repository.authorization;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.base.authority.model.authorization.RoleMenusAuthorizationVersion;

public interface RoleMenusAuthorizationVersionRepository extends Repository<RoleMenusAuthorizationVersion, Long> {

    @Modifying
    @Query("update RoleMenusAuthorizationVersion v set v.version = v.version + 1 where v.id = 1")
    void addVersion();

    @Query("select v from RoleMenusAuthorizationVersion v where v.id = 1")
    RoleMenusAuthorizationVersion findOne();

    RoleMenusAuthorizationVersion save(RoleMenusAuthorizationVersion version);
}
