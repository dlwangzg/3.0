package com.leadingsoft.bizfuse.base.uap.dto;

import com.leadingsoft.bizfuse.common.web.dto.AbstractDTO;

/**
 * 认证用的用户DTO
 *
 * @author liuyg
 */
public class AuthenticationUserDTO extends AbstractDTO {

    // 用户编号
    private String no;
    // 基本信息
    private String name;
    private String nickname;
    // 账号信息
    private String mobile;
    private String email;
    private String loginId;
    private String password;

    // 微信账号信息
    private String unionId;
    private String subscriptionOpenId; // 微信公众号openId
    private String mobileAppOpenId; // 手机App OpenId
    private String websiteAppOpenId; // 网站应用 OpenId

    // 帐号状态
    private boolean enabled;
    private boolean accountLocked;
    private boolean accountExpired;
    private boolean credentialsExpired;

    // 一次性认证Token
    private String nonceToken;

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public Boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountLocked() {
        return this.accountLocked;
    }

    public void setAccountLocked(final boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public boolean isAccountExpired() {
        return this.accountExpired;
    }

    public void setAccountExpired(final boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    public boolean isCredentialsExpired() {
        return this.credentialsExpired;
    }

    public void setCredentialsExpired(final boolean credentialsExpired) {
        this.credentialsExpired = credentialsExpired;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public String getUnionId() {
        return this.unionId;
    }

    public void setUnionId(final String unionId) {
        this.unionId = unionId;
    }

    public String getSubscriptionOpenId() {
        return this.subscriptionOpenId;
    }

    public void setSubscriptionOpenId(final String subscriptionOpenId) {
        this.subscriptionOpenId = subscriptionOpenId;
    }

    public String getMobileAppOpenId() {
        return this.mobileAppOpenId;
    }

    public void setMobileAppOpenId(final String mobileAppOpenId) {
        this.mobileAppOpenId = mobileAppOpenId;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public String getNo() {
        return this.no;
    }

    public void setNo(final String no) {
        this.no = no;
    }

    public String getLoginId() {
        return this.loginId;
    }

    public void setLoginId(final String loginId) {
        this.loginId = loginId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getWebsiteAppOpenId() {
        return this.websiteAppOpenId;
    }

    public void setWebsiteAppOpenId(final String websiteAppOpenId) {
        this.websiteAppOpenId = websiteAppOpenId;
    }

    public String getNonceToken() {
        return nonceToken;
    }

    public void setNonceToken(String nonceToken) {
        this.nonceToken = nonceToken;
    }
}
