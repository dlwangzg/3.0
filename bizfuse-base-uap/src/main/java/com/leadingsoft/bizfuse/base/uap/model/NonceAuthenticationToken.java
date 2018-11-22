package com.leadingsoft.bizfuse.base.uap.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;

import com.leadingsoft.bizfuse.common.jpa.model.AbstractModel;

import lombok.Getter;
import lombok.Setter;

/**
 * 一次性认证令牌（用于APP端自动登录）
 * 
 * @author liuyg
 *
 */
@Setter
@Getter
@Entity
public class NonceAuthenticationToken extends AbstractModel {

    private static final long serialVersionUID = 8667514302142315292L;

    /**
     * 用户编号（同样类型的设备，用户编号+设备ID不能重复，如果重复，删除旧的Token记录）
     */
    @NotBlank
    @Length(max = 20)
    @Column(nullable = false, updatable = false, length = 20)
    private String no;

    /**
     * 设备ID
     */
    @NotBlank
    @Length(max = 80)
    private String deviceId;

    /**
     * 设备类型
     */
    @NotBlank
    @Length(max = 32)
    private String deviceType;

    /**
     * 操作系统类型
     */
    @NotBlank
    @Length(max = 32)
    private String osType;

    /**
     * 操作系统版本
     */
    @NotBlank
    @Length(max = 32)
    private String osVersion;

    /**
     * 软件类型
     */
    @Length(max = 32)
    private String softwareType;

    /**
     * 软件版本
     */
    @Length(max = 32)
    private String softwareVersion;

    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

    /**
     * 过期时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredDate;
    
    public boolean isExpired() {
    	return System.currentTimeMillis() > this.expiredDate.getTime();
    }
}
