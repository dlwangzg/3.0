package com.leadingsoft.bizfuse.base.authority.model.authorization;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import com.leadingsoft.bizfuse.base.authority.model.role.Role;
import com.leadingsoft.bizfuse.common.jpa.model.AbstractAuditModel;

/**
 * 角色的菜单权限
 * <p>
 * 给角色分配菜单权限
 *
 * @author Administrator
 */
@Entity
public class RoleMenusAuthorization extends AbstractAuditModel {
    private static final long serialVersionUID = 5878138846590977440L;

    @OneToOne
    private Role role;

    @Lob
    private String authorizeMenus;//授权的menuID列表，以 ”,“ 分割

    public String getAuthorizeMenus() {
        return this.authorizeMenus;
    }

    public void setAuthorizeMenus(final String authorizeMenus) {
        this.authorizeMenus = authorizeMenus;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

}
