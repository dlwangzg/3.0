package com.leadingsoft.bizfuse.cloud.saas.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.common.web.dto.AbstractDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 租户数据源配置
 *
 * @author liuyg
 */
@Getter
@Setter
public class TenantDataSourceConfigDTO extends AbstractDTO {

    private static final long serialVersionUID = 2522066098867403693L;

    /**
     * 租户编号
     */
    @ApiModelProperty("租户编号")
    @NotBlank
    @Length(max = 20)
    private String tenantNo;

    /**
     * 服务器类型
     */
    @ApiModelProperty("服务器类型")
    @NotBlank
    @Length(max = 20)
    private String serverType;

    /**
     * 数据库驱动类
     */
    @ApiModelProperty("数据库驱动类")
    @NotBlank
    @Length(max = 200)
    private String driverClassName;

    /**
     * 数据库URL
     */
    @ApiModelProperty("数据库URL")
    @NotBlank
    @Length(max = 200)
    private String url;

    /**
     * 数据库用户名
     */
    @ApiModelProperty("数据库用户名")
    @NotBlank
    @Length(max = 200)
    private String username;

    /**
     * 数据库密码
     */
    @ApiModelProperty("数据库密码")
    @NotBlank
    @Length(max = 20)
    private String password;
}
