package com.leadingsoft.bizfuse.base.uap.dto;

import org.springframework.util.StringUtils;

public class AuthenticationDTO {

    // 账号信息
    private String username;
    private String password;

    // 自动登录一次性Token
    private String nonceToken;

    // 自动登录信息
    private String deviceId;
    private String deviceType;
    private String osType;
    private String osVersion;
    private String softwareType;
    private String softwareVersion;
    private String sessionId;

    // 微信账号信息
    private String unionId;
    private String subscriptionOpenId; // 微信公众号openId
    private String mobileAppOpenId; // 手机App OpenId
    private String websiteAppOpenId; // 网站应用 OpenId

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

    public String getWebsiteAppOpenId() {
        return this.websiteAppOpenId;
    }

    public void setWebsiteAppOpenId(final String websiteAppOpenId) {
        this.websiteAppOpenId = websiteAppOpenId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getNonceToken() {
        return this.nonceToken;
    }

    public void setNonceToken(final String nonceToken) {
        this.nonceToken = nonceToken;
    }

    public String getDeviceId() {
        return this.deviceId == null ? "" : this.deviceId;
    }

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return this.deviceType == null ? "" : this.deviceType;
    }

    public void setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOsType() {
        return this.osType == null ? "" : this.osType;
    }

    public void setOsType(final String osType) {
        this.osType = osType;
    }

    public String getOsVersion() {
        return this.osVersion == null ? "" : this.osVersion;
    }

    public void setOsVersion(final String osVersion) {
        this.osVersion = osVersion;
    }

    public String getSoftwareType() {
        return this.softwareType == null ? "" : this.softwareType;
    }

    public void setSoftwareType(final String softwareType) {
        this.softwareType = softwareType;
    }

    public String getSoftwareVersion() {
        return this.softwareVersion == null ? "" : this.softwareVersion;
    }

    public void setSoftwareVersion(final String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean needNonceToken() {
		return StringUtils.hasText(this.deviceId);
	}
}
