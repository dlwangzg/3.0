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
 * 服务实例
 *
 * @author liuyg
 */
@Getter
@Setter
public class ServerInstanceDTO extends AbstractDTO {

    private static final long serialVersionUID = -2699733883780227993L;

    /**
     * 服务器类型
     */
    @ApiModelProperty("服务器类型")
    @NotBlank
    @Length(max = 20)
    private String type;

    /**
     * 服务器内网IP
     */
    @ApiModelProperty("服务器内网IP")
    @NotBlank
    @Length(max = 20)
    private String internalIP;

    /**
     * 服务器公网IP
     */
    @ApiModelProperty("服务器公网IP")
    @Length(max = 20)
    private String publicIP;

    /**
     * 服务器备注
     */
    @ApiModelProperty("服务器备注")
    @Length(max = 200)
    private String remarks;

    /**
     * 服务器端口
     */
    @ApiModelProperty("服务器端口")
    @NotNull
    @Min(1)
    private Integer port;
}
