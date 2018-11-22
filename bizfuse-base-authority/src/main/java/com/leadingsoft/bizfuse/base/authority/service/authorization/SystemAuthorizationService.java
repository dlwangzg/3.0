package com.leadingsoft.bizfuse.base.authority.service.authorization;

import java.util.List;
import java.util.Set;

import com.leadingsoft.bizfuse.base.authority.model.authorization.RoleMenusAuthorization;
import com.leadingsoft.bizfuse.base.authority.model.authorization.UserGrantedRole;
import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu;
import com.leadingsoft.bizfuse.base.authority.model.role.Role;

/**
 * 系统授权服务接口
 *
 * @author liuyg
 */
public interface SystemAuthorizationService {

    /**
     * 保存角色的授权菜单列表
     *
     * @param systemAuthorization
     * @return
     */
    RoleMenusAuthorization saveOrUpdate(RoleMenusAuthorization systemAuthorization);

    /**
     * 根据角色ID获取角色的菜单授权
     *
     * @param roleId
     * @return
     */
    RoleMenusAuthorization getMenusAuthorization(Long roleId);

    /**
     * 删除角色的菜单授权
     *
     * @param roleId
     */
    void deleteMenusAuthorization(Long roleId);

    /**
     * 获取用户授权的角色列表
     *
     * @param userNo
     * @return
     */
    List<Role> getUserRoles(String userNo);

    /**
     * 获取用户授权的菜单列表
     *
     * @param userNo
     * @return
     */
    List<SystemMenu> getUserAuthorizedMenus(String userNo);

    /**
     * 获取用户授权的菜单按钮列表
     *
     * @param userNo
     * @return
     */
    List<SystemMenu> getUserAuthorizedButtons(String userNo);

    /**
     * 获取用户未授权的菜单按钮列表
     *
     * @param userNo
     * @return
     */
    List<SystemMenu> getUserUnauthorizedButtons(String userNo);

    /**
     * 获取用户授权的菜单列表
     *
     * @param userNo
     * @return
     */
    Set<Long> getUserAuthorizedMenuIds(String userNo);

    /**
     * 分配角色给用户
     *
     * @param userNo
     * @param role
     * @return
     */
    UserGrantedRole grantRoleToUser(String userNo, Role role);

    /**
     * 收回分配给用户的角色
     *
     * @param userNo
     * @param role
     * @return
     */
    boolean revokeRoleFromUser(String userNo, Role role);

    /**
     * 收回所有用户已分配的特定角色
     *
     * @param role 要收回的角色
     */
    void revokeUsersRole(Role role);

    /**
     * 更新用户角色
     *
     * @param userNo
     * @param roleIds
     * @return
     */
    boolean updateUserRoles(String userNo, List<Long> roleIds);
}
