package com.leadingsoft.bizfuse.base.authority.controller.authorization;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.leadingsoft.bizfuse.base.authority.dto.authorization.RoleMenusAuthorizationConvertor;
import com.leadingsoft.bizfuse.base.authority.dto.authorization.RoleMenusAuthorizationDTO;
import com.leadingsoft.bizfuse.base.authority.dto.authorization.UserRolesAuthorizationDTO;
import com.leadingsoft.bizfuse.base.authority.dto.menu.SystemMenuListConvertor;
import com.leadingsoft.bizfuse.base.authority.dto.menu.SystemMenuListDTO;
import com.leadingsoft.bizfuse.base.authority.dto.role.RoleConvertor;
import com.leadingsoft.bizfuse.base.authority.dto.role.RoleDTO;
import com.leadingsoft.bizfuse.base.authority.model.authorization.RoleMenusAuthorization;
import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu;
import com.leadingsoft.bizfuse.base.authority.model.role.Role;
import com.leadingsoft.bizfuse.base.authority.service.authorization.SystemAuthorizationService;
import com.leadingsoft.bizfuse.common.web.dto.result.ListResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;
import com.leadingsoft.bizfuse.common.webauth.annotation.CurrentUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/w/authorizations")
@Api(tags = {"权限管理 -> 授权管理API" })
public class SystemAuthorizationController {

    @Autowired
    private SystemAuthorizationService systemAuthorizationService;

    @Autowired
    private RoleMenusAuthorizationConvertor systemAuthorizationConvertor;

    @Autowired
    private SystemMenuListConvertor systemMenuListConvertor;

    @Autowired
    private RoleConvertor roleConvertor;

    /**
     * 更新角色的授权菜单列表
     *
     * @param systemAuthorizationDTO
     * @return
     */
    @Timed
    @ApiOperation("更新指定角色的可访问菜单列表")
    @RequestMapping(value = "/roleMenus/{roleId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<Void> saveOrUpdateRoleMenus(@PathVariable final long roleId,
            @RequestBody @Valid final RoleMenusAuthorizationDTO systemAuthorizationDTO) {
        systemAuthorizationDTO.setId(roleId);
        final RoleMenusAuthorization systemAuthorization = this.systemAuthorizationConvertor
                .toModel(systemAuthorizationDTO);

        this.systemAuthorizationService.saveOrUpdate(systemAuthorization);

        return ResultDTO.success();
    }

    /**
     * 获取角色的授权菜单列表
     *
     * @param roleId
     * @return
     */
    @Timed
    @ApiOperation("获取指定角色的可访问菜单列表")
    @RequestMapping(value = "/roleMenus/{roleId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<RoleMenusAuthorizationDTO> getRoleMenus(@PathVariable final Long roleId) {
        final RoleMenusAuthorization systemAuthorization =
                this.systemAuthorizationService.getMenusAuthorization(roleId);

        final ResultDTO<RoleMenusAuthorizationDTO> resultDTO = this.systemAuthorizationConvertor
                .toResultDTO(systemAuthorization);
        return resultDTO;
    }

    /**
     * 更新用户的角色列表（用户授权）
     *
     * @param userRolesDTO
     * @return
     */
    @Timed
    @ApiOperation("更新指定用户的角色列表")
    @RequestMapping(value = "/userRoles/{userNo}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<Void> saveOrUpdateUserRoles(@PathVariable final String userNo,
            @RequestBody @Valid final UserRolesAuthorizationDTO userRolesDTO) {
        userRolesDTO.setUserNo(userNo);
        this.systemAuthorizationService.updateUserRoles(userRolesDTO.getUserNo(), userRolesDTO.getRoleIds());
        return ResultDTO.success();
    }

    /**
     * 获取用户的角色列表
     *
     * @param userNo
     * @return
     */
    @Timed
    @ApiOperation("获取指定用户的角色列表")
    @RequestMapping(value = "/userRoles/{userNo}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResultDTO<RoleDTO> getUserRoles(@PathVariable final String userNo) {
        final List<Role> userRoles = this.systemAuthorizationService.getUserRoles(userNo);
        return this.roleConvertor.toResultDTO(userRoles);
    }

    /**
     * 获取我的授权菜单
     *
     * @param roleId
     * @return
     */
    @Timed
    @ApiOperation(value = "获取 ”当前登录用户“ 的可访问菜单列表", notes = "只有登录用户可以访问，数据用于前端页面根据权限定制用户界面")
    @RequestMapping(value = "/myAuthorizedMenus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResultDTO<SystemMenuListDTO> getMyAuthorizedMenus(@CurrentUser final String userNo) {

        final Collection<SystemMenu> menus = this.systemAuthorizationService.getUserAuthorizedMenus(userNo);
        if (menus.isEmpty()) {
            throw new CustomRuntimeException("406", "您目前没有权限，如需访问系统，请联系管理员分配权限");
        }

        final List<SystemMenuListDTO> menuListDTO = this.systemMenuListConvertor.toAuthorizedMenuListDTO(menus);
        return ListResultDTO.success(menuListDTO);
    }

    /**
     * 获取我授权的角色列表
     *
     * @param userNo
     * @return
     */
    @Timed
    @ApiOperation(value = "获取 “当前登录用户“ 的角色列表", notes = "只有登录用户可以访问，数据用于前端页面根据角色定制用户界面")
    @RequestMapping(value = "/myRoles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResultDTO<RoleDTO> getMyRoles(@CurrentUser final String userNo) {
        final List<Role> roles = this.systemAuthorizationService.getUserRoles(userNo);
        return this.roleConvertor.toResultDTO(roles);
    }

    /**
     * 获取我的授权的按钮Keys
     *
     * @param roleId
     * @return
     */
    @Timed
    @ApiOperation(value = "获取 ”当前登录用户“ 的可访问菜单按钮Keys", notes = "只有登录用户可以访问，数据用于前端页面根据权限定制用户界面")
    @RequestMapping(value = "/myAuthorizedButtonKeys", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResultDTO<String> getMyAuthorizedButtons(@CurrentUser final String userNo) {

        final Collection<SystemMenu> buttons = this.systemAuthorizationService.getUserAuthorizedButtons(userNo);

        final List<String> keys = buttons.stream().map(button -> button.getKey()).collect(Collectors.toList());
        return ListResultDTO.success(keys);
    }

    /**
     * 获取我的未授权的按钮Keys
     *
     * @param roleId
     * @return
     */
    @Timed
    @ApiOperation(value = "获取 ”当前登录用户“ 的可访问菜单按钮Keys", notes = "只有登录用户可以访问，数据用于前端页面根据权限定制用户界面")
    @RequestMapping(value = "/myUnauthorizedButtonKeys", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResultDTO<String> getMyUnauthorizedButtons(@CurrentUser final String userNo) {

        final Collection<SystemMenu> buttons = this.systemAuthorizationService.getUserUnauthorizedButtons(userNo);

        final List<String> keys = buttons.stream().map(button -> button.getKey()).collect(Collectors.toList());
        return ListResultDTO.success(keys);
    }

}
