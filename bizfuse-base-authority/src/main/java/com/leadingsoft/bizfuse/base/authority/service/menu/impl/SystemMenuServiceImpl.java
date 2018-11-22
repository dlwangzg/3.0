package com.leadingsoft.bizfuse.base.authority.service.menu.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu;
import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu.MenuType;
import com.leadingsoft.bizfuse.base.authority.repository.menu.SystemMenuRepository;
import com.leadingsoft.bizfuse.base.authority.service.menu.SystemMenuService;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

@Service
public class SystemMenuServiceImpl implements SystemMenuService {

    @Autowired
    private SystemMenuRepository systemMenuRepository;

    @Override
    public List<SystemMenu> findBaseMenuList(final String menuTitle) {
        if (StringUtils.hasText(menuTitle)) {
            return this.systemMenuRepository
                    .findByParentIdIsNullAndTypeAndTitleContainingOrderBySortNumAsc(MenuType.site, menuTitle);
        } else {
            return this.systemMenuRepository.findByParentIdIsNullAndTypeOrderBySortNumAsc(MenuType.site);
        }
    }

    @Override
    public List<SystemMenu> findSubMenuList(final Long menuId, final String menuTitle) {
        final SystemMenu model = this.systemMenuRepository.findOne(menuId);
        List<SystemMenu> subMenus = model.getSubMenus();
        if (StringUtils.hasText(menuTitle)) {
            subMenus = subMenus.stream().filter(menu -> {
                return menu.getTitle().contains(menuTitle);
            }).collect(Collectors.toList());
        }
        return subMenus;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"systemMenus" }, allEntries = true)
    public void deleteById(final Long menuId) {
        this.systemMenuRepository.delete(menuId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"systemMenus" }, allEntries = true)
    public SystemMenu create(final SystemMenu systemMenu) {
        return this.systemMenuRepository.save(systemMenu);
    }

    @Override
    @Cacheable(value = "systemMenus", key = "#menuId")
    public SystemMenu findOne(final Long menuId) {
        return this.systemMenuRepository.findOne(menuId);
    }

    /**
     * 变更父菜单
     */
    @Override
    @Transactional
    @CacheEvict(value = {"systemMenus" }, allEntries = true)
    public void changeMenuParent(final Long menuId, final Long toParentMenuId) {
        final SystemMenu systemMenu = this.systemMenuRepository.findOne(menuId);
        final SystemMenu parentSystemMenu = this.systemMenuRepository.findOne(toParentMenuId);

        systemMenu.setParent(parentSystemMenu);

        this.systemMenuRepository.save(systemMenu);

    }

    /**
     * 菜单的移动(向上或者向下移动)
     */
    @Override
    @Transactional
    @CacheEvict(value = {"systemMenus" }, allEntries = true)
    public void moveMenu(final Long menuId, final Move action) {
        final SystemMenu systemMenu = this.systemMenuRepository.findOne(menuId);
        final SystemMenu parentMenu = systemMenu.getParent();
        int index = -1;
        List<SystemMenu> baseMenuList = new ArrayList<SystemMenu>();

        if (parentMenu == null) {
            baseMenuList = this.systemMenuRepository.findByParentIdIsNullAndTypeOrderBySortNumAsc(MenuType.site);

        } else {
            // 不是根菜单
            baseMenuList = parentMenu.getSubMenus();
        }
        if ((baseMenuList == null) || (baseMenuList.size() == 0)) {
            return;
        }
        // 根菜单
        if (Move.up == action) {// 向上移动

            for (final SystemMenu menu : baseMenuList) {
                index++;
                if (menu.getId() == systemMenu.getId()) {
                    // 当前操作的menu的处理
                    int newSortNum = systemMenu.getSortNum();
                    systemMenu.setSortNum(newSortNum - 1);
                    this.systemMenuRepository.save(systemMenu);

                    // 上面的menu的处理
                    final SystemMenu upMenu = baseMenuList.get(index - 1);
                    newSortNum = upMenu.getSortNum();
                    upMenu.setSortNum(newSortNum + 1);
                    this.systemMenuRepository.save(upMenu);
                    break;
                }
            }
        } else if (Move.down == action) {// 向下移动
            for (final SystemMenu menu : baseMenuList) {
                index++;
                if (menu.getId() == systemMenu.getId()) {
                    // 当前操作的menu的处理
                    int newSortNum = systemMenu.getSortNum();
                    systemMenu.setSortNum(newSortNum + 1);
                    this.systemMenuRepository.save(systemMenu);

                    // 上面的menu的处理
                    final SystemMenu upMenu = baseMenuList.get(index - 1);
                    newSortNum = upMenu.getSortNum();
                    upMenu.setSortNum(newSortNum - 1);
                    this.systemMenuRepository.save(upMenu);
                    break;
                }
            }

        } else {
            throw new CustomRuntimeException("请求参数action错误");
        }
    }

    @Override
    @Cacheable(value = "systemMenuActions")
    public List<SystemMenu> getAllMenuActions() {
        return this.systemMenuRepository.findAllByTypeAndEnabledIsTrue(MenuType.url);
    }

    public static enum Move {
        up, down
    }

}
