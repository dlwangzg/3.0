package com.leadingsoft.bizfuse.cloud.saas.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.leadingsoft.bizfuse.cloud.saas.server.BizfuseSaasConfigServerConfigration;

/**
 * 启用Bizfuse SaaS 配置服务模块
 *
 * @author liuyg
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(BizfuseSaasConfigServerConfigration.class)
public @interface EnableBizfuseSaasConfigServer {
}
