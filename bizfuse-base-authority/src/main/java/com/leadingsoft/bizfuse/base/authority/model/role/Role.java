package com.leadingsoft.bizfuse.base.authority.model.role;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.common.jpa.model.AbstractAuditModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Role extends AbstractAuditModel {
    private static final long serialVersionUID = -2352002565071613131L;

    /**
     * 角色名称
     */
    @NotBlank
    @Column(unique = true)
    private String name;

    /**
     * 角色描述
     */
    @Column
    private String description;
}
