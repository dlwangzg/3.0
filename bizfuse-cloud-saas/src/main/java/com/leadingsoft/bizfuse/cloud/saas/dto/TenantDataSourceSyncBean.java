package com.leadingsoft.bizfuse.cloud.saas.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantDataSourceSyncBean {

    private String serverType;

    private List<String> tenantNos;

    private Long lastModifiedTime;
}
