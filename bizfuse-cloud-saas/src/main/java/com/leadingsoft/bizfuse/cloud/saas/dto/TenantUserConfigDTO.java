package com.leadingsoft.bizfuse.cloud.saas.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.common.web.dto.AbstractDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 租户用户配置信息
 *
 * @author liuyg
 */
@Getter
@Setter
public class TenantUserConfigDTO extends AbstractDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编号
     */
    @ApiModelProperty("租户编号")
    @NotBlank
    @Length(max = 20)
    private String tenantNo;

    /**
     * 用户编号
     */
    @ApiModelProperty("用户编号")
    @NotBlank
    @Length(max = 20)
    private String userNo;
}
