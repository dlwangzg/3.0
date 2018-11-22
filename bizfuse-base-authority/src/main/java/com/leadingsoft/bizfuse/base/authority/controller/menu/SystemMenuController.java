package com.leadingsoft.bizfuse.base.authority.controller.menu;

import java.util.List;
import java.util.stream.Collectors;

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
import com.leadingsoft.bizfuse.base.authority.dto.menu.SystemMenuConvertor;
import com.leadingsoft.bizfuse.base.authority.dto.menu.SystemMenuDTO;
import com.leadingsoft.bizfuse.base.authority.dto.menu.SystemMenuListConvertor;
import com.leadingsoft.bizfuse.base.authority.dto.menu.SystemMenuListDTO;
import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu;
import com.leadingsoft.bizfuse.base.authority.model.menu.SystemMenu.MenuType;
import com.leadingsoft.bizfuse.base.authority.repository.menu.SystemMenuRepository;
import com.leadingsoft.bizfuse.base.authority.service.menu.SystemMenuService;
import com.leadingsoft.bizfuse.base.authority.service.menu.impl.SystemMenuServiceImpl.Move;
import com.leadingsoft.bizfuse.common.web.dto.SortableTreeNodeDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.ListResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.PageResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.support.Searchable;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * web端菜单的管理
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/w/sys/menus")
@Api(tags = {"权限管理 -> 系统菜单管理API" })
public class SystemMenuController {

    @Autowired
    private SystemMenuService systemMenuService;

    @Autowired
    private SystemMenuConvertor systemMenuConvertor;

    @Autowired
    private SystemMenuListConvertor systemMenuListConvertor;

    @Autowired
    private SystemMenuRepository systemMenuRepository;

    /**
     * 取得所有的菜单列表（菜单->子菜单列表结构）
     *
     * @return
     */
    @Timed
    @ApiOperation("取得所有的菜单列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResultDTO<SystemMenuListDTO> listAll() {
        final List<SystemMenu> modelList = this.systemMenuService.findBaseMenuList(null);

        final ListResultDTO<SystemMenuListDTO> resultDTO = this.systemMenuListConvertor.toResultDTO(modelList);
        return resultDTO;
    }

    /**
     * 取得菜单详细
     *
     * @return
     */
    @Timed
    @ApiOperation("取得菜单详细")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<SystemMenuDTO> get(@PathVariable final Long id) {
        final SystemMenu model = this.systemMenuService.findOne(id);
        return this.systemMenuConvertor.toResultDTO(model);
    }

    /**
     * 取得菜单的JSON-TREE
     *
     * @return
     */
    @Timed
    @ApiOperation(value = "取得菜单的JSON-TREE", notes = "根节点为虚构的节点，ID为-1. 仅返回生效的菜单集合")
    @RequestMapping(value = "/nodes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResultDTO<SortableTreeNodeDTO> listJsonTree() {
        final List<SystemMenu> modelList = this.systemMenuRepository.findAllByEnabledIsTrue();

        final SortableTreeNodeDTO root = new SortableTreeNodeDTO();
        root.setId(-1l);
        root.setText("菜单结构");
        root.setSortNum(0);
        final List<SortableTreeNodeDTO> menusTree = modelList.stream().map(menu -> {
            final SortableTreeNodeDTO node = new SortableTreeNodeDTO();
            node.setId(menu.getId());
            node.setText(menu.getTitle());
            node.setSortNum(menu.getSortNum());
            if (menu.getParent() != null) {
                node.setParent(menu.getParent().getId());
            } else {
                node.setParent(-1l);
            }
            return node;
        }).collect(Collectors.toList());
        menusTree.add(0, root);
        return ListResultDTO.success(menusTree);
    }

    /**
     * 根据条件查询根菜单列表
     *
     * @param menuTitle 查询的条件(可选)
     * @return
     */
    @Timed
    @ApiOperation(value = "根据条件查询一级菜单列表", notes = "根据菜单名称模糊查询一级菜单列表，过滤条件可选 ")
    @RequestMapping(value = "/baseMenus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResultDTO<SystemMenuDTO> listFirstLevelMenus(
            @RequestParam(value = "query", required = false) final String menuTitle) {
        // 查询所有的根菜单列表
        final List<SystemMenu> list = this.systemMenuService.findBaseMenuList(menuTitle);
        final ListResultDTO<SystemMenuDTO> resultDTO = this.systemMenuConvertor.toResultDTO(list);
        return resultDTO;
    }

    /**
     * 根据父菜单ID查询子菜单列表
     *
     * @param menuId 菜单ID
     * @param menuTitle 查询的条件(可选)
     * @return
     */
    @Timed
    @ApiOperation(value = "根据父菜单ID查询子菜单列表", notes = "可根据菜单名称模糊查询子菜单列表，过滤条件可选 ")
    @RequestMapping(value = "/{id}/subMenus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResultDTO<SystemMenuDTO> listByMenuId(@PathVariable(value = "id") final Long menuId,
            @RequestParam(value = "query", required = false) final String menuTitle) {
        // 查询menuId下的根菜单列表
        final List<SystemMenu> list = this.systemMenuService.findSubMenuList(menuId, menuTitle);

        final ListResultDTO<SystemMenuDTO> resultDTO = this.systemMenuConvertor.toResultDTO(list);
        return resultDTO;
    }

    /**
     * 创建菜单
     *
     * @return
     */
    @Timed
    @ApiOperation(value = "创建菜单")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<SystemMenuDTO> create(@RequestBody @Valid final SystemMenuDTO dto) {
        final SystemMenu systemMenu = this.systemMenuConvertor.toModel(dto);
        this.systemMenuService.create(systemMenu);
        return this.systemMenuConvertor.toResultDTO(systemMenu);
    }

    /**
     * 更新菜单
     *
     * @return
     */
    @Timed
    @ApiOperation(value = "更新菜单")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<SystemMenuDTO> update(@PathVariable("id") final Long menuId,
            @RequestBody @Valid final SystemMenuDTO dto) {
        dto.setId(menuId);
        final SystemMenu systemMenu = this.systemMenuConvertor.toModel(dto);
        this.systemMenuService.create(systemMenu);
        return this.systemMenuConvertor.toResultDTO(systemMenu);
    }

    /**
     * 根据菜单ID删除菜单
     *
     * @param menuId 菜单ID
     * @return
     */
    @Timed
    @ApiOperation(value = "删除菜单")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<?> delete(@PathVariable("id") final Long menuId) {

        this.systemMenuService.deleteById(menuId);
        final ResultDTO<?> success = ResultDTO.success();
        return success;
    }

    /**
     * 调整菜单的位置(父菜单)
     *
     * @param menuId 菜单ID
     * @param targetMenuId 调整到的MenuID
     * @return
     */
    @Timed
    @ApiOperation(value = "修改菜单的父菜单")
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<?> adjustMenuLocationToParent(@PathVariable("id") final Long menuId,
            @RequestParam("targetId") final Long targetMenuId) {
        this.systemMenuService.changeMenuParent(menuId, targetMenuId);

        final ResultDTO<?> success = ResultDTO.success();
        return success;
    }

    /**
     * 调整菜单的位置
     *
     * @param menuId 菜单ID
     * @param targetMenuId 调整到的MenuID
     * @return
     */
    @Timed
    @ApiOperation(value = "调整菜单的位置", notes = "同级菜单之间，上下移动菜单的位置。action=up上移一位，action=down下移一位")
    @RequestMapping(value = "/{id}/move", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<?> adjustMenuLocationForUpOrDown(@PathVariable("id") final Long menuId,
            @RequestParam("action") final Move action) {
        this.systemMenuService.moveMenu(menuId, action);

        final ResultDTO<?> success = ResultDTO.success();
        return success;
    }

    /**
     * 获取ＲＵＬ（ＡＰＩ）菜单列表
     *
     * @return
     */
    @Timed
    @ApiOperation(value = "获取URL(API)列表", notes = "API权限管理的接口之一")
    @RequestMapping(value = "/urlMenus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResultDTO<SystemMenuDTO> listUrlMenus(final Searchable searchable, final Pageable pageable) {
        // 查询menuId下的根菜单列表
        Page<SystemMenu> page = null;
        if (!searchable.hasKey("title")) {
            page = this.systemMenuRepository.findAllByTypeAndEnabledIsTrue(MenuType.url, pageable);
        } else {
            page = this.systemMenuRepository.findAllByTypeAndEnabledIsTrueAndTitleContaining(MenuType.url,
                    searchable.getStrValue("title"), pageable);
        }
        final PageResultDTO<SystemMenuDTO> resultDTO = this.systemMenuConvertor.toResultDTO(page);
        return resultDTO;
    }
}
