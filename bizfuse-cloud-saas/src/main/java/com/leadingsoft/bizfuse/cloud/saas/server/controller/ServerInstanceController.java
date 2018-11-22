package com.leadingsoft.bizfuse.cloud.saas.server.controller;

import java.util.Collections;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.leadingsoft.bizfuse.cloud.saas.dto.ServerInstanceDTO;
import com.leadingsoft.bizfuse.cloud.saas.dto.ServerUsageBean;
import com.leadingsoft.bizfuse.cloud.saas.dto.SyncBean;
import com.leadingsoft.bizfuse.cloud.saas.server.convertor.ServerInstanceConvertor;
import com.leadingsoft.bizfuse.cloud.saas.server.model.ServerInstance;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.ServerInstanceRepository;
import com.leadingsoft.bizfuse.cloud.saas.server.service.ServerInstanceService;
import com.leadingsoft.bizfuse.common.web.dto.result.ListResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.PageResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.support.Searchable;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * ServerInstance的管理接口
 *
 * @author auto
 */
@Slf4j
@RestController
@RequestMapping("/saas/serverInstances")
@Api(tags = {"ServerInstance管理API" })
public class ServerInstanceController {
    @Autowired
    private ServerInstanceService serverInstanceService;
    @Autowired
    private ServerInstanceRepository serverInstanceRepository;
    @Autowired
    private ServerInstanceConvertor serverInstanceConvertor;

    /**
     * 获取分页数据
     *
     * @param pageable 分页+排序参数
     * @return 分页数据
     */
    @Timed
    @ApiOperation(value = "获取分页数据", notes = "")
    @RequestMapping(value = "/p", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResultDTO<ServerInstanceDTO> search(final Pageable pageable, final Searchable searchable) {
        final Page<ServerInstance> models = this.serverInstanceRepository.search(pageable, searchable);
        return this.serverInstanceConvertor.toResultDTO(models);
    }

    /**
     * 获取服务器租户使用情况的分页数据
     *
     * @param pageable 分页
     * @return 分页数据
     */
    @Timed
    @ApiOperation(value = "获取服务器租户使用情况的分页数据", notes = "")
    @RequestMapping(value = "/usage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResultDTO<ServerUsageBean> searchUsage(final Pageable pageable, final Searchable searchable) {
        final Page<ServerUsageBean> models = this.serverInstanceRepository.searchUsage(pageable, searchable);
        return PageResultDTO.success(models);
    }

    /**
     * 同步数据
     *
     * @param lastModifiedTime 上次变更时间
     * @return 所有数据
     */
    @Timed
    @ApiOperation(value = "同步数据", notes = "")
    @RequestMapping(value = "/sync/{lastModifiedTime}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResultDTO<ServerInstanceDTO> sync(@PathVariable final Long lastModifiedTime) {
        final SyncBean<List<ServerInstance>> bean =
                this.serverInstanceService.getAllIfExistModification(lastModifiedTime);
        if (bean == null) {
            return ListResultDTO.success(Collections.emptyList());
        }
        final ListResultDTO<ServerInstanceDTO> rs = this.serverInstanceConvertor.toResultDTO(bean.getData());
        rs.setTimestamp(new Date(bean.getLastModifiedTime()));
        return rs;
    }

    /**
     * 取得详细数据
     *
     * @param id 资源ID
     * @return 资源详细
     */
    @Timed
    @ApiOperation(value = "获取详细数据", notes = "")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<ServerInstanceDTO> get(@PathVariable final Long id) {
        final ServerInstance model = this.serverInstanceService.get(id);
        return this.serverInstanceConvertor.toResultDTO(model);
    }

    /**
     * 新建操作
     *
     * @param serverInstanceDTO 新建资源的DTO
     * @return 新建资源
     */
    @Timed
    @ApiOperation(value = "新建操作", notes = "")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<ServerInstanceDTO> create(@RequestBody @Valid final ServerInstanceDTO serverInstanceDTO) {
        final ServerInstance model = this.serverInstanceConvertor.toModel(serverInstanceDTO);
        this.serverInstanceService.create(model);
        if (ServerInstanceController.log.isInfoEnabled()) {
            ServerInstanceController.log.info("{} instance {} was created.", ServerInstance.class.getSimpleName(),
                    model.getId());
        }
        return this.serverInstanceConvertor.toResultDTO(model);
    }

    /**
     * 更新操作
     *
     * @param id 更新资源的ID
     * @param serverInstanceDTO 更新资源的DTO
     * @return 更新后资源
     */
    @Timed
    @ApiOperation(value = "更新操作", notes = "")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<ServerInstanceDTO> update(@PathVariable final Long id,
            @RequestBody @Valid final ServerInstanceDTO serverInstanceDTO) {
        serverInstanceDTO.setId(id);
        final ServerInstance model = this.serverInstanceConvertor.toModel(serverInstanceDTO);
        this.serverInstanceService.update(model);
        if (ServerInstanceController.log.isInfoEnabled()) {
            ServerInstanceController.log.info("{} instance {} was updated.", ServerInstance.class.getSimpleName(),
                    model.getId());
        }
        return this.serverInstanceConvertor.toResultDTO(model);
    }

    /**
     * 删除操作
     *
     * @param Id 资源ID
     * @return 操作结果
     */
    @Timed
    @ApiOperation(value = "删除操作", notes = "")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<Void> delete(@PathVariable final Long id) {
        this.serverInstanceService.delete(id);
        if (ServerInstanceController.log.isInfoEnabled()) {
            ServerInstanceController.log.info("{} instance {} was deleted.", ServerInstance.class.getSimpleName(), id);
        }
        return ResultDTO.success();
    }
}
