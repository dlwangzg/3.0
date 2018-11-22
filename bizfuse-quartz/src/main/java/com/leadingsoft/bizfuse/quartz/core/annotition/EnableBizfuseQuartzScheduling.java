package com.leadingsoft.bizfuse.quartz.core.annotition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.leadingsoft.bizfuse.quartz.BizfuseQuartzConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnableAsync
@EnableScheduling
@Import(BizfuseQuartzConfiguration.class)
@Documented
@Configuration
public @interface EnableBizfuseQuartzScheduling {

}
