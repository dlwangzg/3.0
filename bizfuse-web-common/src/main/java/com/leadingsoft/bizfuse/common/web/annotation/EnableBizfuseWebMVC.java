package com.leadingsoft.bizfuse.common.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.leadingsoft.bizfuse.common.web.config.BizfuseWebConfiguration;

/**
 * 启用 Bizfuse WebMVC
 *
 * @author liuyg
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(value = {BizfuseWebConfiguration.class })
@Documented
public @interface EnableBizfuseWebMVC {
}
