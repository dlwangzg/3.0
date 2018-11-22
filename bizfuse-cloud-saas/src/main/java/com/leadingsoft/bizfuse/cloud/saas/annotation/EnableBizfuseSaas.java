package com.leadingsoft.bizfuse.cloud.saas.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.leadingsoft.bizfuse.cloud.saas.client.BizfuseSaasConfigration;

/**
 * 启用Bizfuse SaaS 模块
 *
 * @author liuyg
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(BizfuseSaasConfigration.class)
public @interface EnableBizfuseSaas {
}
