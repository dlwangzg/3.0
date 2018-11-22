package com.leadingsoft.bizfuse.cloud.saas.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 服务实例使用情况
 *
 * @author liuyg
 */
@Getter
@Setter
public class ServerUsageBean {

    /**
     * 服务实例ID
     */
    private long id;
    /**
     * 类型
     */
    private String type;
    /**
     * 内网地址
     */
    private String internalIP;
    /**
     * 端口
     */
    private int port;
    /**
     * 资源共享的租户数量
     */
    private long tenantCount;
}
