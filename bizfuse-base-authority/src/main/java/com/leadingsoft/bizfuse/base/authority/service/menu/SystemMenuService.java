package com.leadingsoft.bizfuse.base.authority.service.menu;

import java.util.List;

import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu;
import com.leadingsoft.bizfuse.base.authority.service.menu.impl.SystemMenuServiceImpl.Move;

/**
 * 系统菜单服务接口
 *
 * @author liuyg
 */
public interface SystemMenuService {

    /**
     * 根据菜单名称过滤，获取根菜单（一级菜单）列表
     *
     * @param menuTitle
     * @return
     */
    public List<SystemMenu> findBaseMenuList(final String menuTitle);

    public List<SystemMenu> findSubMenuList(Long menuId, String menuTitle);

    public void deleteById(Long menuId);

    public SystemMenu create(SystemMenu systemMenu);

    public SystemMenu findOne(Long menuId);

    public void changeMenuParent(Long menuId, Long toMenuId);

    public void moveMenu(Long menuId, Move action);

    public List<SystemMenu> getAllMenuActions();
}
