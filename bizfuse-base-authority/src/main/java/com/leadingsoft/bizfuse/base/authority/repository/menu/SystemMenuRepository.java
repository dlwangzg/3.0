package com.leadingsoft.bizfuse.base.authority.repository.menu;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu;
import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu.MenuType;

public interface SystemMenuRepository extends Repository<SystemMenu, Long> {

    List<SystemMenu> findByParentIdIsNullAndTypeOrderBySortNumAsc(final MenuType type);

    List<SystemMenu> findByParentIdIsNullAndTypeAndTitleContainingOrderBySortNumAsc(final MenuType site,
            final String menuTitle);

    SystemMenu findOne(final Long menuId);

    SystemMenu findByParentIdAndTitleContaining(final Long menuId, final String menuTitle);

    void delete(final Long menuId);

    SystemMenu save(final SystemMenu systemMenu);

    List<SystemMenu> findAllByIdIn(final Collection<Long> ids);

    List<SystemMenu> findAllByTypeAndIdInAndEnabledIsTrue(MenuType type, final Collection<Long> ids);

    List<SystemMenu> findAllByParentIsNull();

    List<SystemMenu> findAllByEnabledIsTrue();

    Long count();

    List<SystemMenu> findAllByTypeAndEnabledIsTrue(MenuType button);

    Page<SystemMenu> findAllByTypeAndEnabledIsTrue(MenuType button, Pageable pageable);

    Page<SystemMenu> findAllByTypeAndEnabledIsTrueAndTitleContaining(MenuType type, String title, Pageable pageable);

    List<SystemMenu> findAllByTypeAndIdIn(MenuType button, Set<Long> menuIds);

    List<SystemMenu> findAllByTypeAndIdNotIn(MenuType button, Set<Long> menuIds);
}
