package com.leadingsoft.bizfuse.cloud.saas.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.common.jpa.model.AbstractAuditModel;

import lombok.Getter;
import lombok.Setter;

/**
 * 租户用户配置信息
 *
 * @author liuyg
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name = "", unique = true, columnList = "tenantNo,userNo") })
public class TenantUserConfig extends AbstractAuditModel {

    private static final long serialVersionUID = 8424504404061307244L;

    /**
     * 租户编号
     */
    @NotBlank
    @Length(max = 20)
    @Column(unique = false, nullable = false, length = 20)
    private String tenantNo;

    /**
     * 用户编号
     */
    @NotBlank
    @Length(max = 20)
    @Column(unique = false, nullable = false, length = 20)
    private String userNo;
}
