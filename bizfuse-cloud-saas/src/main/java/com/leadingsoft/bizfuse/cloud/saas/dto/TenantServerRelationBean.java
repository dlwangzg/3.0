package com.leadingsoft.bizfuse.cloud.saas.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantServerRelationBean implements Serializable {

    private static final long serialVersionUID = -2531257948315485926L;

    private String tenantNo;

    private Long serverInstanceId;
}
