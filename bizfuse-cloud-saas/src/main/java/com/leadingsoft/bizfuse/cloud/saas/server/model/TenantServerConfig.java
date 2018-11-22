package com.leadingsoft.bizfuse.cloud.saas.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.common.jpa.model.AbstractAuditModel;

import lombok.Getter;
import lombok.Setter;

/**
 * 租户服务器配置信息
 *
 * @author liuyg
 */
@Getter
@Setter
@Entity
public class TenantServerConfig extends AbstractAuditModel {

    private static final long serialVersionUID = -5772057575965706079L;

    /**
     * 租户编号
     */
    @NotBlank
    @Length(max = 20)
    @Column(unique = false, nullable = false, length = 20)
    private String tenantNo;

    @ManyToOne
    private ServerInstance serverInstance;
}
