package com.leadingsoft.bizfuse.base.uap.dto;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 微信注册 DTO (仅作入参)
 */
public class WeChatRegisterDTO {

    /**
     * 注册渠道
     * <p>
     * <li>微信公众号
     * <li>微信移动应用
     * <li>微信网站应用
     */
    public static enum WeChatChannel {
        officialAccount, wechatMobileApp, wechatWebsite;
    }

    private String unionId;

    @NotBlank
    private String openId;

    /**
     * 渠道 1:网站应用 2:手机APP 3:微信公众帐号
     */
    private WeChatChannel channel;

    public String getUnionId() {
        return this.unionId;
    }

    public void setUnionId(final String unionId) {
        this.unionId = unionId;
    }

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(final String openId) {
        this.openId = openId;
    }

    public WeChatChannel getChannel() {
        return this.channel;
    }

    public void setChannel(final WeChatChannel channel) {
        this.channel = channel;
    }
}
