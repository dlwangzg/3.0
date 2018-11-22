package com.leadingsoft.bizfuse.base.authority.dto.authorization;

import java.util.List;

import com.leadingsoft.bizfuse.common.web.dto.AbstractDTO;

public class UserRolesAuthorizationDTO extends AbstractDTO {

    private String userNo;

    private List<Long> roleIds;

    public List<Long> getRoleIds() {
        return this.roleIds;
    }

    public void setRoleIds(final List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public String getUserNo() {
        return this.userNo;
    }

    public void setUserNo(final String userNo) {
        this.userNo = userNo;
    }
}
