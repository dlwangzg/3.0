package com.leadingsoft.bizfuse.base.authority.dto.menu;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu;
import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu.MenuType;
import com.leadingsoft.bizfuse.common.web.dto.AbstractConvertor;

@Component
public class SystemMenuListConvertor extends AbstractConvertor<SystemMenu, SystemMenuListDTO> {

    @Override
    public SystemMenu toModel(final SystemMenuListDTO dto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SystemMenuListDTO toDTO(final SystemMenu model, final boolean forListView) {
        final SystemMenuListDTO systemMenuListDTO = new SystemMenuListDTO();

        systemMenuListDTO.setEnabled(model.isEnabled());
        systemMenuListDTO.setId(model.getId());
        systemMenuListDTO.setTitle(model.getTitle());
        systemMenuListDTO.setKey(model.getKey());
        systemMenuListDTO.setHref(model.getHref());
        systemMenuListDTO.setClassName(model.getClassName());
        systemMenuListDTO.setType(model.getType());
        final Collection<SystemMenu> subMenus = model.getSubMenus();
        if ((subMenus != null) && (subMenus.size() > 0)) {
            final List<SystemMenuListDTO> children =
                    subMenus.stream().sorted((m1, m2) -> {
                        return m1.getSortNum() - m2.getSortNum();
                    }).map(p -> this.toDTO(p)).collect(Collectors.toList());
            systemMenuListDTO.setSubMenus(children);
        }
        return systemMenuListDTO;
    }

    public SystemMenuListDTO toDTO(final SystemMenu model, final Predicate<SystemMenu> filter) {
        final SystemMenuListDTO systemMenuListDTO = new SystemMenuListDTO();
        systemMenuListDTO.setEnabled(model.isEnabled());
        systemMenuListDTO.setId(model.getId());
        systemMenuListDTO.setTitle(model.getTitle());
        systemMenuListDTO.setKey(model.getKey());
        systemMenuListDTO.setHref(model.getHref());
        systemMenuListDTO.setClassName(model.getClassName());
        systemMenuListDTO.setType(model.getType());
        final Collection<SystemMenu> subMenus = model.getSubMenus();
        if ((subMenus != null) && (subMenus.size() > 0)) {
            final List<SystemMenuListDTO> children =
                    subMenus.stream().filter(filter).map(p -> this.toDTO(p))
                            .collect(Collectors.toList());
            systemMenuListDTO.setSubMenus(children);
        }
        return systemMenuListDTO;
    }

    public final List<SystemMenuListDTO> toListDTO(final Collection<SystemMenu> collector,
            final Predicate<SystemMenu> filter) {

        return collector.stream().filter(menu -> {
            return (menu.getParent() == null) && menu.isEnabled();
        }).sorted((m1, m2) -> {
            return m1.getSortNum() - m2.getSortNum();
        }).map(model -> this.toDTO(model, filter)).collect(Collectors.toList());
    }

    public List<SystemMenuListDTO> toAuthorizedMenuListDTO(final Collection<SystemMenu> menus) {
        final Set<SystemMenu> allMenus = new HashSet<>();
        menus.stream().filter(menu -> {
            return (menu.getType() != MenuType.url);
        }).forEach(menu -> {
            SystemMenu tmp = menu;
            while (tmp.getType() == MenuType.button) {
                tmp = menu.getParent();
            }
            allMenus.add(tmp);
            while (tmp.getParent() != null) {
                allMenus.add(tmp.getParent());
                tmp = tmp.getParent();
            }
        });
        return allMenus.stream().filter(menu -> {
            return menu.getParent() == null;
        }).sorted((m1, m2) -> {
            return m1.getSortNum() - m2.getSortNum();
        }).map(rootMenu -> {
            return this.toAuthorizedMenuListDTO(rootMenu, allMenus);
        }).collect(Collectors.toList());
    }

    private SystemMenuListDTO toAuthorizedMenuListDTO(final SystemMenu model, final Set<SystemMenu> allMenus) {
        final SystemMenuListDTO systemMenuListDTO = new SystemMenuListDTO();
        systemMenuListDTO.setEnabled(model.isEnabled());
        systemMenuListDTO.setId(model.getId());
        systemMenuListDTO.setTitle(model.getTitle());
        systemMenuListDTO.setKey(model.getKey());
        systemMenuListDTO.setHref(model.getHref());
        systemMenuListDTO.setClassName(model.getClassName());
        systemMenuListDTO.setType(model.getType());
        final List<SystemMenuListDTO> subMenusDTO = allMenus.stream().filter(menu -> {
            return menu.getParent() == model;
        }).sorted((m1, m2) -> {
            return m1.getSortNum() - m2.getSortNum();
        }).map(p -> this.toAuthorizedMenuListDTO(p, allMenus))
                .collect(Collectors.toList());
        if (!subMenusDTO.isEmpty()) {
            systemMenuListDTO.setSubMenus(subMenusDTO);
        }
        return systemMenuListDTO;
    }
}
