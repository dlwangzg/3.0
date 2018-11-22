package com.leadingsoft.bizfuse.cloud.saas.server.controller;

import java.util.ArrayList;
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
import com.leadingsoft.bizfuse.cloud.saas.dto.SyncBean;
import com.leadingsoft.bizfuse.cloud.saas.dto.TenantServerConfigDTO;
import com.leadingsoft.bizfuse.cloud.saas.dto.TenantServerRelationBean;
import com.leadingsoft.bizfuse.cloud.saas.server.convertor.TenantServerConfigConvertor;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantServerConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.TenantServerConfigRepository;
import com.leadingsoft.bizfuse.cloud.saas.server.service.TenantServerConfigService;
import com.leadingsoft.bizfuse.common.web.dto.result.ListResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.PageResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.support.Searchable;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * TenantServerConfig的管理接口
 *
 * @author auto
 */
@Slf4j
@RestController
@RequestMapping("/saas/tenantServerConfigs")
@Api(tags = {"TenantServerConfig管理API" })
public class TenantServerConfigController {
    @Autowired
    private TenantServerConfigService tenantServerConfigService;
    @Autowired
    private TenantServerConfigRepository tenantServerConfigRepository;
    @Autowired
    private TenantServerConfigConvertor tenantServerConfigConvertor;

    /**
     * 获取分页数据
     *
     * @param pageable 分页+排序参数
     * @param searchable 过滤参数
     * @return 分页数据
     */
    @Timed
    @ApiOperation(value = "获取分页数据", notes = "")
    @RequestMapping(value = "/p", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResultDTO<TenantServerConfigDTO> page(final Pageable pageable, final Searchable searchable) {
        final Page<TenantServerConfig> models = this.tenantServerConfigRepository.page(pageable, searchable);
        return this.tenantServerConfigConvertor.toResultDTO(models);
    }

    /**
     * 获取所有数据
     *
     * @param searchable 过滤参数
     * @return 过滤后所有数据
     */
    @Timed
    @ApiOperation(value = "获取所有数据", notes = "")
    @RequestMapping(value = "/sync/{lastModifiedTime}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResultDTO<TenantServerRelationBean> sync(@PathVariable final Long lastModifiedTime) {
        final SyncBean<List<TenantServerRelationBean>> bean =
                this.tenantServerConfigService.getAllIfExistModification(lastModifiedTime);
        if (bean == null) {
            return ListResultDTO.success(Collections.emptyList());
        }
        final ListResultDTO<TenantServerRelationBean> rs = ListResultDTO.success(bean.getData());
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
    public ResultDTO<TenantServerConfigDTO> get(@PathVariable final Long id) {
        final TenantServerConfig model = this.tenantServerConfigService.get(id);
        return this.tenantServerConfigConvertor.toResultDTO(model);
    }

    /**
     * 新建操作
     *
     * @param tenantServerConfigDTO 新建资源的DTO
     * @return 新建资源
     */
    @Timed
    @ApiOperation(value = "新建操作", notes = "")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<TenantServerConfigDTO> create(
            @RequestBody @Valid final TenantServerConfigDTO tenantServerConfigDTO) {
        final TenantServerConfig model = this.tenantServerConfigConvertor.toModel(tenantServerConfigDTO);
        this.tenantServerConfigService.create(model);
        if (TenantServerConfigController.log.isInfoEnabled()) {
            TenantServerConfigController.log.info("{} instance {} was created.",
                    TenantServerConfig.class.getSimpleName(), model.getId());
        }
        return this.tenantServerConfigConvertor.toResultDTO(model);
    }

    /**
     * 更新操作
     *
     * @param id 更新资源的ID
     * @param tenantServerConfigDTO 更新资源的DTO
     * @return 更新后资源
     */
    @Timed
    @ApiOperation(value = "更新操作", notes = "")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<TenantServerConfigDTO> update(@PathVariable final Long id,
            @RequestBody @Valid final TenantServerConfigDTO tenantServerConfigDTO) {
        tenantServerConfigDTO.setId(id);
        final TenantServerConfig model = this.tenantServerConfigConvertor.toModel(tenantServerConfigDTO);
        this.tenantServerConfigService.update(model);
        if (TenantServerConfigController.log.isInfoEnabled()) {
            TenantServerConfigController.log.info("{} instance {} was updated.",
                    TenantServerConfig.class.getSimpleName(), model.getId());
        }
        return this.tenantServerConfigConvertor.toResultDTO(model);
    }

    /**
     * 批量更新操作
     * 
     * @param tenantNo
     * @param type
     * @param serverIds
     * @return
     */
    @ApiOperation(value = "批量更新操作", notes = "")
    @RequestMapping(value = "/{tenantNo}/serveType/{type}/updateAll", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<Void> updateAll(
            @PathVariable final String tenantNo,
            @PathVariable final String type,
            @RequestBody @Valid final List<Long> serverIds) {

        final Searchable search = new Searchable();
        search.put("tenantNo", tenantNo);
        search.put("serverType", type);
        final List<TenantServerConfig> configs = this.tenantServerConfigRepository.list(search);
        final List<Long> oldServerIds = new ArrayList<>();
        // delete batch
        configs.stream().filter(config -> {
            if (serverIds.contains(config.getServerInstance().getId())) {
                oldServerIds.add(config.getServerInstance().getId());
                return false;
            } else {
                return true;
            }
        }).forEach(config -> {
            this.tenantServerConfigService.delete(config.getId());
        });
        // insert batch
        serverIds.stream().filter(newId -> !oldServerIds.contains(newId)).forEach(newId -> {
            final TenantServerConfigDTO dto = new TenantServerConfigDTO();
            dto.setTenantNo(tenantNo);
            dto.setServerId(newId);
            final TenantServerConfig model = this.tenantServerConfigConvertor.toModel(dto);
            this.tenantServerConfigService.create(model);
            if (TenantServerConfigController.log.isInfoEnabled()) {
                TenantServerConfigController.log.info("{} instance {} was created.",
                        TenantServerConfig.class.getSimpleName(), model.getId());
            }
        });
        return ResultDTO.success();
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
        this.tenantServerConfigService.delete(id);
        if (TenantServerConfigController.log.isInfoEnabled()) {
            TenantServerConfigController.log.info("{} instance {} was deleted.",
                    TenantServerConfig.class.getSimpleName(), id);
        }
        return ResultDTO.success();
    }
}
