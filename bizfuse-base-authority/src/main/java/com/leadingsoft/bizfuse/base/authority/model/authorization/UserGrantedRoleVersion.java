package com.leadingsoft.bizfuse.base.authority.model.authorization;

import javax.persistence.Entity;

import com.leadingsoft.bizfuse.common.jpa.model.AssignIdModel;

/**
 * 用户授予的角色版本变更记录
 *
 * @author liuyg
 */
@Entity
public class UserGrantedRoleVersion extends AssignIdModel<Long> {

    private static final long serialVersionUID = 3785915510693182708L;

    public UserGrantedRoleVersion() {
        this.id = 1l;
        this.version = 1;
    }

    private final long version;

    public long getVersion() {
        return this.version;
    }
}
