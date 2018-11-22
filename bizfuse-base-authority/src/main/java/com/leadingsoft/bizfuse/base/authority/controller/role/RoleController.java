package com.leadingsoft.bizfuse.base.authority.controller.role;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.leadingsoft.bizfuse.base.authority.dto.role.RoleConvertor;
import com.leadingsoft.bizfuse.base.authority.dto.role.RoleDTO;
import com.leadingsoft.bizfuse.base.authority.model.role.Role;
import com.leadingsoft.bizfuse.base.authority.repository.role.RoleRepository;
import com.leadingsoft.bizfuse.base.authority.service.role.RoleService;
import com.leadingsoft.bizfuse.common.web.dto.result.ListResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.PageResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * Role的管理接口
 *
 * @author auto
 */
@Slf4j
@RestController
@RequestMapping("/w/roles")
@Api(tags = {"权限管理 -> 角色管理API" })
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleConvertor roleConvertor;

    /**
     * 取得分页数据
     *
     * @param pageable 分页+排序参数
     * @return 分页数据
     */
    @Timed
    @ApiOperation("取得角色分页数据")
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResultDTO<RoleDTO> page(final Pageable pageable) {
        final Page<Role> models = this.roleRepository.findAll(pageable);
        return this.roleConvertor.toResultDTO(models);
    }

    /**
     * 取得角色数据列表
     *
     * @param 过滤条件
     * @return 角色数据列表
     */
    @Timed
    @ApiOperation("取得角色列表，参数s可用于过滤名称")
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResultDTO<RoleDTO> list(final @RequestParam(required = false) String s) {
        if (s == null) {
            final List<Role> roles = this.roleRepository.findAll();
            return this.roleConvertor.toResultDTO(roles);
        } else {
            final List<Role> roles = this.roleRepository.findByNameContaining(s);
            return this.roleConvertor.toResultDTO(roles);
        }
    }

    /**
     * 取得详细数据
     *
     * @param id 资源ID
     * @return 资源详细
     */
    @Timed
    @ApiOperation("取得角色详细数据")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<RoleDTO> get(@PathVariable final Long id) {
        final Role model = this.roleService.getRole(id);
        return this.roleConvertor.toResultDTO(model);
    }

    /**
     * 新建操作
     *
     * @param roleDTO 新建资源的DTO
     * @return 新建资源
     */
    @Timed
    @ApiOperation("新建角色")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<RoleDTO> create(@RequestBody @Valid final RoleDTO roleDTO) {
        final Role model = this.roleConvertor.toModel(roleDTO);
        this.roleService.createRole(model);
        if (RoleController.log.isInfoEnabled()) {
            RoleController.log.info("{} instance {} was created.", Role.class.getSimpleName(), model.getId());
        }
        return this.roleConvertor.toResultDTO(model);
    }

    /**
     * 更新操作
     *
     * @param roleDTO 更新资源的DTO
     * @return 更新后资源
     */
    @Timed
    @ApiOperation(value = "更新角色", notes = "只有description（角色描述）可以更新")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<RoleDTO> update(@PathVariable final Long id, @RequestBody final RoleDTO roleDTO) {
        roleDTO.setId(id);
        final Role model = this.roleConvertor.toModel(roleDTO);
        this.roleService.updateRole(model);
        if (RoleController.log.isInfoEnabled()) {
            RoleController.log.info("{} instance {} was updated.", Role.class.getSimpleName(), model.getId());
        }
        return this.roleConvertor.toResultDTO(model);
    }

    /**
     * 删除操作
     *
     * @param Id 资源ID
     * @return
     */
    @Timed
    @ApiOperation(value = "删除角色", notes = "删除角色，并级联删除角色的所有菜单授权，回收所有用户分配的该角色")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<Void> delete(@PathVariable final Long id) {
        this.roleService.deleteById(id);
        if (RoleController.log.isInfoEnabled()) {
            RoleController.log.info("{} instance {} was deleted.", Role.class.getSimpleName(), id);
        }
        return ResultDTO.success();
    }
}
