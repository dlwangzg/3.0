package com.leadingsoft.bizfuse.base.authority.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.leadingsoft.bizfuse.base.authority.BizfuseAuthorityConfiguration;

/**
 * 启用Bizfuse角色权限模块
 *
 * @author liuyg
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(BizfuseAuthorityConfiguration.class)
public @interface EnableBizfuseAuthority {
}
