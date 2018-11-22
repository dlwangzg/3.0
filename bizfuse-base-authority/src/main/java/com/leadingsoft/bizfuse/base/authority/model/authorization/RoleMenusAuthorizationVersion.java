package com.leadingsoft.bizfuse.base.authority.model.authorization;

import javax.persistence.Entity;

import com.leadingsoft.bizfuse.common.jpa.model.AssignIdModel;

/**
 * 角色的菜单权限
 * <p>
 * 给角色分配菜单权限
 *
 * @author Administrator
 */
@Entity
public class RoleMenusAuthorizationVersion extends AssignIdModel<Long> {
    private static final long serialVersionUID = 1051616309743541510L;

    private final long version;

    public RoleMenusAuthorizationVersion() {
        this.id = 1l;
        this.version = 1;
    }

    public long getVersion() {
        return this.version;
    }

}
