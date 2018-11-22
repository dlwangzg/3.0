package com.leadingsoft.bizfuse.base.filestorage.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.leadingsoft.bizfuse.base.filestorage.BizfuseStorageConfiguration;

/**
 * 启用Bizfuse数据字典(码表)模块
 *
 * @author liuyg
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(BizfuseStorageConfiguration.class)
public @interface EnableBizfuseStorage {
}
