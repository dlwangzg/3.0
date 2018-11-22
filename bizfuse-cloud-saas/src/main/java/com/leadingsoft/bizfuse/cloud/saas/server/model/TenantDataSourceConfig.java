package com.leadingsoft.bizfuse.cloud.saas.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.common.jpa.model.AbstractAuditModel;

import lombok.Getter;
import lombok.Setter;

/**
 * 租户数据源配置
 *
 * @author liuyg
 */
@Getter
@Setter
@Entity
public class TenantDataSourceConfig extends AbstractAuditModel {

    private static final long serialVersionUID = -389140128186326617L;

    /**
     * 租户编号
     */
    @NotBlank
    @Length(max = 20)
    @Column(unique = false, nullable = false, length = 20)
    private String tenantNo;

    /**
     * 服务器类型
     */
    @NotBlank
    @Length(max = 20)
    @Column(unique = false, nullable = false, length = 20)
    private String serverType;

    /**
     * 数据库驱动类
     */
    @NotBlank
    @Length(max = 200)
    @Column(unique = false, nullable = false, length = 200)
    private String driverClassName;

    /**
     * 数据库URL
     */
    @NotBlank
    @Length(max = 200)
    @Column(unique = false, nullable = false, length = 200)
    private String url;

    /**
     * 数据库用户名
     */
    @NotBlank
    @Length(max = 200)
    @Column(unique = false, nullable = false, length = 200)
    private String username;

    /**
     * 数据库密码
     */
    @NotBlank
    @Length(max = 20)
    @Column(unique = false, nullable = false, length = 20)
    private String password;
}
