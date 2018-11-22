package com.leadingsoft.bizfuse.cloud.saas.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.common.web.dto.AbstractDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 租户服务器配置信息
 *
 * @author liuyg
 */
@Getter
@Setter
public class TenantServerConfigDTO extends AbstractDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编号
     */
    @ApiModelProperty("租户编号")
    @NotBlank
    @Length(max = 20)
    private String tenantNo;

    /**
     * 租户编号
     */
    @ApiModelProperty("服务器ID")
    @NotNull
    private Long serverId;

    /**
     * 服务器类型
     */
    @ApiModelProperty("服务器类型")
    @Length(max = 20)
    private String serverType;

    /**
     * 服务器内网IP
     */
    @ApiModelProperty("服务器内网IP")
    @Length(max = 20)
    private String serverInternalIP;

    /**
     * 服务器公网IP
     */
    @ApiModelProperty("服务器公网IP")
    @Length(max = 20)
    private String serverPublicIP;

    /**
     * 服务器端口
     */
    @ApiModelProperty("服务器端口")
    @Min(1)
    private Integer port;

    /**
     * 服务器备注
     */
    @ApiModelProperty("服务器备注")
    @Length(max = 200)
    private String serverRemarks;
}
