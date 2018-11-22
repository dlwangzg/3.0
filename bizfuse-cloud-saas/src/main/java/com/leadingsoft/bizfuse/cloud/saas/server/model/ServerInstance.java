package com.leadingsoft.bizfuse.cloud.saas.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.common.jpa.model.AbstractAuditModel;

import lombok.Getter;
import lombok.Setter;

/**
 * 服务实例
 *
 * @author liuyg
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(unique = true, name = "uk_internalIP_port", columnList = "internalIP,port") })
public class ServerInstance extends AbstractAuditModel {

    private static final long serialVersionUID = 8106309941036042513L;

    /**
     * 服务器类型
     */
    @NotBlank
    @Length(max = 20)
    @Column(unique = false, nullable = false, length = 20)
    private String type;

    /**
     * 服务器内网IP
     */
    @NotBlank
    @Length(max = 20)
    @Column(unique = false, nullable = false, length = 20)
    private String internalIP;

    /**
     * 服务器公网IP
     */
    @Length(max = 20)
    @Column(unique = false, nullable = true, length = 20)
    private String publicIP;

    /**
     * 服务器备注
     */
    @Length(max = 200)
    @Column(unique = false, nullable = true, length = 200)
    private String remarks;

    /**
     * 服务器端口
     */
    @NotNull
    @Min(1)
    private Integer port;
}
