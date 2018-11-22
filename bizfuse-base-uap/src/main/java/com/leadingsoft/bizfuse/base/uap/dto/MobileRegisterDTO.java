package com.leadingsoft.bizfuse.base.uap.dto;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * 手机注册 DTO (仅作入参)
 */
@Getter
@Setter
public class MobileRegisterDTO {
    /**
     * 手机号码
     */
	@NotBlank
	@Pattern(regexp="^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$", message="手机号格式错误")
    private String mobile;

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
