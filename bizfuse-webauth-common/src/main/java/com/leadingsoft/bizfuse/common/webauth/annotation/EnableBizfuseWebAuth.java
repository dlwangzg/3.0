package com.leadingsoft.bizfuse.common.webauth.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.leadingsoft.bizfuse.common.webauth.config.BizfuseWebAuthConfigurer;

/**
 * 启用 Bizfuse 安全认证模块
 *
 * @author liuyg
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(value = {BizfuseWebAuthConfigurer.class })
@Documented
public @interface EnableBizfuseWebAuth {

}
