package com.leadingsoft.bizfuse.base.authority.repository.authorization;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.base.authority.model.authorization.UserGrantedRoleVersion;

public interface UserGrantedRoleVersionRepository extends Repository<UserGrantedRoleVersion, Long> {

    @Modifying
    @Query("update UserGrantedRoleVersion v set v.version = v.version + 1 where v.id = 1")
    void addVersion();

    @Query("select v from UserGrantedRoleVersion v where v.id = 1")
    UserGrantedRoleVersion findOne();

    UserGrantedRoleVersion save(UserGrantedRoleVersion version);
}
