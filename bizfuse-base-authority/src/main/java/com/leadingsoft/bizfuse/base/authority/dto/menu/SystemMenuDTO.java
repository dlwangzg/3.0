package com.leadingsoft.bizfuse.base.authority.dto.menu;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu.MenuType;
import com.leadingsoft.bizfuse.common.web.dto.AbstractDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemMenuDTO extends AbstractDTO {

    @NotBlank
    private String title;//菜单标题

    private String key;

    private boolean enabled;//true 启用 false 禁用

    private String href;//菜单的URL

    private String className;

    private Long parentId;//父菜单Id

    private String parentTitle;//父菜单标题

    private int sortNum;//序号

    @NotNull
    private MenuType type;
}
