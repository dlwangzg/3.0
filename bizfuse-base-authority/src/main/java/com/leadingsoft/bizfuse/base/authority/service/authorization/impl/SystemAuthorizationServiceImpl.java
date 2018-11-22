package com.leadingsoft.bizfuse.base.authority.service.authorization.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leadingsoft.bizfuse.base.authority.constant.Constants;
import com.leadingsoft.bizfuse.base.authority.model.authorization.RoleMenusAuthorization;
import com.leadingsoft.bizfuse.base.authority.model.authorization.RoleMenusAuthorizationVersion;
import com.leadingsoft.bizfuse.base.authority.model.authorization.UserGrantedRole;
import com.leadingsoft.bizfuse.base.authority.model.authorization.UserGrantedRoleVersion;
import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu;
import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu.MenuType;
import com.leadingsoft.bizfuse.base.authority.model.role.Role;
import com.leadingsoft.bizfuse.base.authority.repository.authorization.RoleMenusAuthorizationRepository;
import com.leadingsoft.bizfuse.base.authority.repository.authorization.RoleMenusAuthorizationVersionRepository;
import com.leadingsoft.bizfuse.base.authority.repository.authorization.UserGrantedRoleRepository;
import com.leadingsoft.bizfuse.base.authority.repository.authorization.UserGrantedRoleVersionRepository;
import com.leadingsoft.bizfuse.base.authority.repository.menu.SystemMenuRepository;
import com.leadingsoft.bizfuse.base.authority.repository.role.RoleRepository;
import com.leadingsoft.bizfuse.base.authority.service.authorization.SystemAuthorizationService;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;

@Service
@Transactional
public class SystemAuthorizationServiceImpl implements SystemAuthorizationService, InitializingBean {

    @Autowired
    private RoleMenusAuthorizationRepository systemAuthorizationRepository;
    @Autowired
    private UserGrantedRoleRepository userGrantedRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private SystemMenuRepository systemMenuRepository;
    @Autowired
    private UserGrantedRoleVersionRepository userGrantedRoleVersionRepository;
    @Autowired
    private RoleMenusAuthorizationVersionRepository roleMenusAuthorizationVersionRepository;

    @Override
    public RoleMenusAuthorization saveOrUpdate(final RoleMenusAuthorization systemAuthorization) {
        this.roleMenusAuthorizationVersionRepository.addVersion();
        return this.systemAuthorizationRepository.save(systemAuthorization);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleMenusAuthorization getMenusAuthorization(final Long id) {
        return this.systemAuthorizationRepository.findOneByRoleId(id);
    }

    @Override
    @Transactional
    public void deleteMenusAuthorization(final Long roleId) {
        this.roleMenusAuthorizationVersionRepository.addVersion();
        this.systemAuthorizationRepository.deleteByRoleId(roleId);
    }

    @Override
    public UserGrantedRole grantRoleToUser(final String userNo, final Role role) {
        final UserGrantedRole authorization = new UserGrantedRole();
        authorization.setUserNo(userNo);
        authorization.setRole(role);
        this.userGrantedRoleVersionRepository.addVersion();
        return this.userGrantedRoleRepository.save(authorization);
    }

    @Override
    public boolean revokeRoleFromUser(final String userNo, final Role role) {
        this.userGrantedRoleRepository.deleteByUserNoAndRoleId(userNo, role.getId());
        this.userGrantedRoleVersionRepository.addVersion();
        return true;
    }

    @Override
    public void revokeUsersRole(final Role role) {
        this.userGrantedRoleRepository.deleteByRole(role);
        this.userGrantedRoleVersionRepository.addVersion();
    }

    @Override
    public boolean updateUserRoles(final String userNo, final List<Long> roleIds) {
        final List<UserGrantedRole> models = this.userGrantedRoleRepository.findAllByUserNo(userNo);
        final Set<Long> existedRoleIds = new HashSet<>();
        // 删除多余的
        models.stream().forEach(model -> {
            if (!roleIds.contains(model.getRole().getId())) {
                this.userGrantedRoleRepository.delete(model.getId());
            } else {
                existedRoleIds.add(model.getRole().getId());
            }
        });
        // 新分配的
        roleIds.stream().filter(roleId -> {
            return !existedRoleIds.contains(roleId);
        }).forEach(roleId -> {
            final Role role = this.roleRepository.findOne(roleId);
            if (role != null) {
                this.grantRoleToUser(userNo, role);
            }
        });
        this.userGrantedRoleVersionRepository.addVersion();
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getUserRoles(final String userNo) {
        final List<UserGrantedRole> roles = this.userGrantedRoleRepository.findAllByUserNo(userNo);
        return roles.stream().map(role -> {
            return role.getRole();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemMenu> getUserAuthorizedMenus(final String userNo) {
        final Collection<Role> roles = this.getUserRoles(userNo);
        if (roles.isEmpty()) {
            return Collections.emptyList();
        }

        List<SystemMenu> authMenus = null;
        if (this.isAdmin(roles)) {
            authMenus = this.systemMenuRepository.findAllByEnabledIsTrue();
        } else {
            final List<RoleMenusAuthorization> authorizations =
                    this.systemAuthorizationRepository.findAllByRoleIn(roles);
            final Set<Long> menuIds = new HashSet<>();
            authorizations.stream().forEach(authorization -> {
                if (authorization.getAuthorizeMenus() != null) {
                    final Long[] menus = JsonUtils.jsonToPojo(authorization.getAuthorizeMenus(), Long[].class);
                    menuIds.addAll(Arrays.asList(menus));
                }
            });
            authMenus = this.systemMenuRepository.findAllByIdIn(menuIds);
        }
        return authMenus;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemMenu> getUserAuthorizedButtons(final String userNo) {
        final Collection<Role> roles = this.getUserRoles(userNo);
        if (roles.isEmpty()) {
            return Collections.emptyList();
        }

        List<SystemMenu> authMenus = null;
        if (this.isAdmin(roles)) {
            authMenus = this.systemMenuRepository.findAllByTypeAndEnabledIsTrue(MenuType.button);
        } else {
            final List<RoleMenusAuthorization> authorizations =
                    this.systemAuthorizationRepository.findAllByRoleIn(roles);
            final Set<Long> menuIds = new HashSet<>();
            authorizations.stream().forEach(authorization -> {
                if (authorization.getAuthorizeMenus() != null) {
                    final Long[] menus = JsonUtils.jsonToPojo(authorization.getAuthorizeMenus(), Long[].class);
                    menuIds.addAll(Arrays.asList(menus));
                }
            });
            authMenus = this.systemMenuRepository.findAllByTypeAndIdIn(MenuType.button, menuIds);
        }
        return authMenus;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemMenu> getUserUnauthorizedButtons(final String userNo) {
        final Collection<Role> roles = this.getUserRoles(userNo);
        if (roles.isEmpty()) {
            return Collections.emptyList();
        }

        List<SystemMenu> authMenus = null;
        if (this.isAdmin(roles)) {
            return Collections.emptyList();
        } else {
            final List<RoleMenusAuthorization> authorizations =
                    this.systemAuthorizationRepository.findAllByRoleIn(roles);
            final Set<Long> menuIds = new HashSet<>();
            authorizations.stream().forEach(authorization -> {
                if (authorization.getAuthorizeMenus() != null) {
                    final Long[] menus = JsonUtils.jsonToPojo(authorization.getAuthorizeMenus(), Long[].class);
                    menuIds.addAll(Arrays.asList(menus));
                }
            });
            authMenus = this.systemMenuRepository.findAllByTypeAndIdNotIn(MenuType.button, menuIds);
        }
        return authMenus;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getUserAuthorizedMenuIds(final String userNo) {
        final Collection<Role> roles = this.getUserRoles(userNo);
        if (roles.isEmpty()) {
            return Collections.emptySet();
        }
        if (this.isAdmin(roles)) {
            final List<SystemMenu> authMenus = this.systemMenuRepository.findAllByEnabledIsTrue();
            return authMenus.stream().map(menu -> {
                return menu.getId();
            }).collect(Collectors.toSet());
        } else {
            final List<RoleMenusAuthorization> authorizations =
                    this.systemAuthorizationRepository.findAllByRoleIn(roles);
            final Set<Long> menuIds = new HashSet<>();
            authorizations.stream().forEach(authorization -> {
                if (authorization.getAuthorizeMenus() != null) {
                    final Long[] menus = JsonUtils.jsonToPojo(authorization.getAuthorizeMenus(), Long[].class);
                    menuIds.addAll(Arrays.asList(menus));
                }
            });
            return menuIds;
        }
    }

    private Boolean isAdmin(final Collection<Role> roles) {
        return roles.stream().anyMatch(role -> {
            return role.getName().equals(Constants.ROLE_ADMIN);
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.userGrantedRoleVersionRepository.findOne() == null) {
            this.userGrantedRoleVersionRepository.save(new UserGrantedRoleVersion());
        }
        if (this.roleMenusAuthorizationVersionRepository.findOne() == null) {
            this.roleMenusAuthorizationVersionRepository.save(new RoleMenusAuthorizationVersion());
        }
    }
}
