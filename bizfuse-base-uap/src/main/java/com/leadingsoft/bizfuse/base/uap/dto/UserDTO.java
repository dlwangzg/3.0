package com.leadingsoft.bizfuse.base.uap.dto;

import java.util.Date;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.leadingsoft.bizfuse.base.uap.enums.Gender;
import com.leadingsoft.bizfuse.common.web.dto.AbstractAuditDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO extends AbstractAuditDTO {

    /**
     * 用户编号，所有系统通用的用户惟一标识
     */
    private String no;

    /**
     * 登录名称
     */
    private String loginId;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 原始密码（仅仅后台管理员创建用户时使用）
     */
    private String password;

    //////////////////////////////////////////////////
    /// 用户的基本信息
    //////////////////////////////////////////////////
    /**
     * 姓名
     */
    private String name;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 地区（国家）
     */
    private String country;

    /**
     * 地区（省）
     */
    private String province;

    /**
     * 地区（市）
     */
    private String city;

    /**
     * 地区（区县）
     */
    private String district;

    /**
     * 地址
     */
    private String address;

    //////////////////////////////////////////////////
    /// 用户的微信关联信息
    //////////////////////////////////////////////////
    /**
     * 用户的微信号
     */
    private String weChatId;

    /**
     * 用户微信号UnionId
     */
    private String unionId;

    /**
     * 用户微信号关注绿信公众号生成的openId
     */
    private String subscriptionOpenId;

    /**
     * 移动应用关联的openId
     */
    private String mobileAppOpenId;

    /**
     * 网站应用关联的OpenId
     */
    private String websiteAppOpenId;

    /////////////////////////////////////////////////////////
    /// 用户帐号状态
    /////////////////////////////////////////////////////////
    /**
     * 帐号是否启用
     */
    private Boolean enabled = true;

    /**
     * 帐号是否锁定
     */
    private Boolean accountLocked;

    /**
     * 帐号是否过期
     */
    private Boolean accountExpired;

    /**
     * 密码是否过期
     */
    private Boolean credentialsExpired;

    /////////////////////////////////////////////////////////
    /// Getter/Setter
    /////////////////////////////////////////////////////////

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

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(final Date birthday) {
        this.birthday = birthday;
    }

    public Gender getGender() {
        return this.gender;
    }

    public void setGender(final Gender gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(final String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(final String district) {
        this.district = district;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getWeChatId() {
        return this.weChatId;
    }

    public void setWeChatId(final String weChatId) {
        this.weChatId = weChatId;
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

    public String getWebsiteAppOpenId() {
        return this.websiteAppOpenId;
    }

    public void setWebsiteAppOpenId(final String websiteAppOpenId) {
        this.websiteAppOpenId = websiteAppOpenId;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAccountLocked() {
        return this.accountLocked;
    }

    public void setAccountLocked(final Boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public Boolean getAccountExpired() {
        return this.accountExpired;
    }

    public void setAccountExpired(final Boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    public Boolean getCredentialsExpired() {
        return this.credentialsExpired;
    }

    public void setCredentialsExpired(final Boolean credentialsExpired) {
        this.credentialsExpired = credentialsExpired;
    }

    @JsonIgnore
    public boolean isMobileBounded() {
        return StringUtils.hasText(this.mobile);
    }

    @JsonIgnore
    @Override
    public final boolean isNew() {
        return !StringUtils.hasText(this.no);
    }
}