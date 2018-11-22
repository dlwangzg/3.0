package com.leadingsoft.bizfuse.base.authority.dto.role;

import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.common.web.dto.AbstractDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO extends AbstractDTO {

    /**
     * 角色名称
     */
    @NotBlank
    @ApiModelProperty(value = "角色名称", position = 1)
    private String name;

    /**
     * 角色描述
     */
    @ApiModelProperty(value = "角色描述", position = 2)
    private String description;
}
