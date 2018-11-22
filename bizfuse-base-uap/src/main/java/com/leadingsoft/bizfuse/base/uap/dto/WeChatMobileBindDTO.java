package com.leadingsoft.bizfuse.base.uap.dto;

import org.hibernate.validator.constraints.NotBlank;

public class WeChatMobileBindDTO {

    /**
     * 用户微信号的OpenId
     */
    @NotBlank
    private String openId;
    /**
     * 用户名
     */
    @NotBlank
    private String mobile;
    /**
     * 密码
     */
    @NotBlank
    private String password;

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(final String openId) {
        this.openId = openId;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
