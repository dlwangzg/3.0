package com.leadingsoft.bizfuse.base.authority.dto.menu;

import java.util.List;

import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu.MenuType;
import com.leadingsoft.bizfuse.common.web.dto.AbstractDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemMenuListDTO extends AbstractDTO {

    private String title;//菜单标题

    private String key;

    private boolean enabled;//true 启用 false 禁用

    private String href;//菜单的URL

    private String className;

    private List<SystemMenuListDTO> subMenus;//子菜单

    private MenuType type;
}
