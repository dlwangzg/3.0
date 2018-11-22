package com.leadingsoft.bizfuse.base.authority.dto.authorization;

import javax.validation.constraints.NotNull;

import com.leadingsoft.bizfuse.common.web.dto.AbstractDTO;

public class RoleMenusAuthorizationDTO extends AbstractDTO {

    @NotNull
    private long[] menuIds = new long[0];

    public long[] getMenuIds() {
        return this.menuIds;
    }

    public void setMenuIds(final long[] menuIds) {
        this.menuIds = menuIds;
    }

}
