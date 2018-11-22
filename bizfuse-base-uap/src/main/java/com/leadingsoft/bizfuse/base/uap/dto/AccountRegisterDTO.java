package com.leadingsoft.bizfuse.base.uap.dto;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * 账号注册 DTO (仅作入参)
 */
@Getter
@Setter
public class AccountRegisterDTO {
    /**
     * 登录ID
     */
    @NotBlank
    private String loginId;

    /**
     * 登录密码
     */
    @NotBlank
    private String password;
    
    /**
     * 真实姓名
     */
    private String name;

    /**
     * 用户别名
     */
    private String nickname;
}
