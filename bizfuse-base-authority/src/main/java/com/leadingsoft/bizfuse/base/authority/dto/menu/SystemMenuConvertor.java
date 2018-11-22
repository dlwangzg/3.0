package com.leadingsoft.bizfuse.base.authority.dto.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu;
import com.leadingsoft.bizfuse.base.authority.repository.menu.SystemMenuRepository;
import com.leadingsoft.bizfuse.common.web.dto.AbstractConvertor;

@Component
public class SystemMenuConvertor extends AbstractConvertor<SystemMenu, SystemMenuDTO> {

    @Autowired
    private SystemMenuRepository systemMenuRepository;

    @Override
    public SystemMenu toModel(final SystemMenuDTO dto) {
        SystemMenu model = new SystemMenu();

        if (dto.getId() != null) {
            model = this.systemMenuRepository.findOne(dto.getId());
            model.setEnabled(dto.isEnabled());
        } else {
            model.setEnabled(true);
        }

        if (dto.getParentId() != null) {
            final SystemMenu parentSystemMenu = this.systemMenuRepository.findOne(dto.getParentId());
            model.setParent(parentSystemMenu);
        }
        model.setSortNum(dto.getSortNum());
        model.setTitle(dto.getTitle());
        model.setKey(dto.getKey());
        model.setHref(dto.getHref());
        model.setClassName(dto.getClassName());
        model.setType(dto.getType());

        return model;
    }

    @Override
    public SystemMenuDTO toDTO(final SystemMenu model, final boolean forListView) {
        final SystemMenuDTO dto = new SystemMenuDTO();
        dto.setEnabled(model.isEnabled());
        dto.setId(model.getId());
        if (model.getParent() != null) {
            dto.setParentId(model.getParent().getId());
            final SystemMenu parentSystemMenu = this.systemMenuRepository.findOne(model.getParent().getId());
            dto.setParentTitle(parentSystemMenu.getTitle());
        }
        dto.setSortNum(model.getSortNum());
        dto.setTitle(model.getTitle());
        dto.setKey(model.getKey());
        dto.setType(model.getType());
        dto.setHref(model.getHref());
        dto.setClassName(model.getClassName());

        return dto;
    }
}
