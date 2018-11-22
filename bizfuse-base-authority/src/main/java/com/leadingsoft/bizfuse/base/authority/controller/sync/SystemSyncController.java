package com.leadingsoft.bizfuse.base.authority.controller.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leadingsoft.bizfuse.base.authority.model.authorization.RoleMenusAuthorization;
import com.leadingsoft.bizfuse.base.authority.model.authorization.RoleMenusAuthorizationVersion;
import com.leadingsoft.bizfuse.base.authority.model.authorization.UserGrantedRole;
import com.leadingsoft.bizfuse.base.authority.model.authorization.UserGrantedRoleVersion;
import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu;
import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu.MenuType;
import com.leadingsoft.bizfuse.base.authority.repository.authorization.RoleMenusAuthorizationRepository;
import com.leadingsoft.bizfuse.base.authority.repository.authorization.RoleMenusAuthorizationVersionRepository;
import com.leadingsoft.bizfuse.base.authority.repository.authorization.UserGrantedRoleRepository;
import com.leadingsoft.bizfuse.base.authority.repository.authorization.UserGrantedRoleVersionRepository;
import com.leadingsoft.bizfuse.base.authority.repository.menu.SystemMenuRepository;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;

import io.swagger.annotations.Api;

/**
 * 系统同步接口
 *
 * @author liuyg
 */
@RestController
@RequestMapping("/systemSync")
@Api(tags = {"权限管理 -> 权限数据同步API" })
public class SystemSyncController {

    private static final TypeReference<List<Long>> listLong = new TypeReference<List<Long>>() {
    };

    @Autowired
    private UserGrantedRoleRepository userGrantedRoleRepository;
    @Autowired
    private RoleMenusAuthorizationRepository roleMenusAuthorizationRepository;
    @Autowired
    private UserGrantedRoleVersionRepository userGrantedRoleVersionRepository;
    @Autowired
    private RoleMenusAuthorizationVersionRepository roleMenusAuthorizationVersionRepository;
    @Autowired
    private SystemMenuRepository systemMenuRepository;

    @Timed
    @RequestMapping(value = "/usersRoles/{version}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<?> getUsersRoles(@PathVariable final long version) {
        final UserGrantedRoleVersion currentVersion = this.userGrantedRoleVersionRepository.findOne();
        if (currentVersion.getVersion() <= version) {
            return ResultDTO.success();
        }
        final List<UserGrantedRole> userRoles = this.userGrantedRoleRepository.findAll();
        final Map<String, List<String>> usersRoles = new HashMap<>();
        userRoles.stream().forEach(userRole -> {
            List<String> roles = usersRoles.get(userRole.getUserNo());
            if (roles == null) {
                roles = new ArrayList<>();
                usersRoles.put(userRole.getUserNo(), roles);
            }
            roles.add(userRole.getRole().getName());
        });
        final ResultDTO<Map<String, List<String>>> rs = ResultDTO.success(usersRoles);
        rs.setTimestamp(new Date(currentVersion.getVersion()));
        return rs;
    }

    @Timed
    @RequestMapping(value = "/actionsRoles/{version}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<?> getActionsRoles(@PathVariable final long version) {
        final RoleMenusAuthorizationVersion currentVersion = this.roleMenusAuthorizationVersionRepository.findOne();
        if (currentVersion.getVersion() <= version) {
            return ResultDTO.success();
        }
        final List<RoleMenusAuthorization> roleMenusList = this.roleMenusAuthorizationRepository.findAll();
        final Map<String, Set<String>> actionsRoles = new HashMap<>();
        roleMenusList.stream().forEach(roleMenus -> {
            final String roleName = roleMenus.getRole().getName();
            final List<Long> menuIds =
                    JsonUtils.jsonToPojoList(roleMenus.getAuthorizeMenus(), SystemSyncController.listLong);
            final List<SystemMenu> actions =
                    this.systemMenuRepository.findAllByTypeAndIdInAndEnabledIsTrue(MenuType.url, menuIds);
            actions.stream().filter(action -> {
                return StringUtils.hasText(action.getHref());
            }).forEach(action -> {
                Set<String> roles = actionsRoles.get(action.getHref());
                if (roles == null) {
                    roles = new HashSet<>();
                    actionsRoles.put(action.getHref(), roles);
                }
                roles.add(roleName);
            });
        });
        final ResultDTO<Map<String, Set<String>>> rs = ResultDTO.success(actionsRoles);
        rs.setTimestamp(new Date(currentVersion.getVersion()));
        return rs;
    }
}
