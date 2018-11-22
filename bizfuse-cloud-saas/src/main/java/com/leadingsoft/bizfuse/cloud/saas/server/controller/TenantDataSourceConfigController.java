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
import com.leadingsoft.bizfuse.cloud.saas.dto.SyncBean;
import com.leadingsoft.bizfuse.cloud.saas.dto.TenantDataSourceConfigDTO;
import com.leadingsoft.bizfuse.cloud.saas.dto.TenantDataSourceSyncBean;
import com.leadingsoft.bizfuse.cloud.saas.server.convertor.TenantDataSourceConfigConvertor;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantDataSourceConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.TenantDataSourceConfigRepository;
import com.leadingsoft.bizfuse.cloud.saas.server.service.TenantDataSourceConfigService;
import com.leadingsoft.bizfuse.common.web.dto.result.ListResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.PageResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.support.Searchable;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * TenantDataSourceConfig的管理接口
 *
 * @author auto
 */
@Slf4j
@RestController
@RequestMapping("/saas/tenantDataSourceConfigs")
@Api(tags = {"TenantDataSourceConfig管理API" })
public class TenantDataSourceConfigController {
    @Autowired
    private TenantDataSourceConfigService tenantDataSourceConfigService;
    @Autowired
    private TenantDataSourceConfigRepository tenantDataSourceConfigRepository;
    @Autowired
    private TenantDataSourceConfigConvertor tenantDataSourceConfigConvertor;

    /**
     * 获取分页数据
     *
     * @param pageable 分页+排序参数
     * @return 分页数据
     */
    @Timed
    @ApiOperation(value = "获取分页数据", notes = "")
    @RequestMapping(value = "/s", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResultDTO<TenantDataSourceConfigDTO> page(final Pageable pageable, final Searchable searchable) {
        final Page<TenantDataSourceConfig> models = this.tenantDataSourceConfigRepository.page(pageable, searchable);
        return this.tenantDataSourceConfigConvertor.toResultDTO(models);
    }

    /**
     * 同步数据
     *
     * @param lastModifiedTime 上次变更时间
     * @return 分页数据
     */
    @Timed
    @ApiOperation(value = "同步数据", notes = "")
    @RequestMapping(value = "/sync", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ListResultDTO<TenantDataSourceConfigDTO> sync(@RequestBody final TenantDataSourceSyncBean search) {
        final SyncBean<List<TenantDataSourceConfig>> bean =
                this.tenantDataSourceConfigService.getAllIfExistModification(search);
        if (bean == null) {
            return ListResultDTO.success(Collections.emptyList());
        }
        final ListResultDTO<TenantDataSourceConfigDTO> rs =
                this.tenantDataSourceConfigConvertor.toResultDTO(bean.getData());
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
    public ResultDTO<TenantDataSourceConfigDTO> get(@PathVariable final Long id) {
        final TenantDataSourceConfig model = this.tenantDataSourceConfigService.get(id);
        return this.tenantDataSourceConfigConvertor.toResultDTO(model);
    }

    /**
     * 新建操作
     *
     * @param tenantDataSourceConfigDTO 新建资源的DTO
     * @return 新建资源
     */
    @Timed
    @ApiOperation(value = "新建操作", notes = "")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<TenantDataSourceConfigDTO> create(
            @RequestBody @Valid final TenantDataSourceConfigDTO tenantDataSourceConfigDTO) {
        final TenantDataSourceConfig model = this.tenantDataSourceConfigConvertor.toModel(tenantDataSourceConfigDTO);
        this.tenantDataSourceConfigService.create(model);
        if (TenantDataSourceConfigController.log.isInfoEnabled()) {
            TenantDataSourceConfigController.log.info("{} instance {} was created.",
                    TenantDataSourceConfig.class.getSimpleName(), model.getId());
        }
        return this.tenantDataSourceConfigConvertor.toResultDTO(model);
    }

    /**
     * 更新操作
     *
     * @param id 更新资源的ID
     * @param tenantDataSourceConfigDTO 更新资源的DTO
     * @return 更新后资源
     */
    @Timed
    @ApiOperation(value = "更新操作", notes = "")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<TenantDataSourceConfigDTO> update(@PathVariable final Long id,
            @RequestBody @Valid final TenantDataSourceConfigDTO tenantDataSourceConfigDTO) {
        tenantDataSourceConfigDTO.setId(id);
        final TenantDataSourceConfig model = this.tenantDataSourceConfigConvertor.toModel(tenantDataSourceConfigDTO);
        this.tenantDataSourceConfigService.update(model);
        if (TenantDataSourceConfigController.log.isInfoEnabled()) {
            TenantDataSourceConfigController.log.info("{} instance {} was updated.",
                    TenantDataSourceConfig.class.getSimpleName(), model.getId());
        }
        return this.tenantDataSourceConfigConvertor.toResultDTO(model);
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
        this.tenantDataSourceConfigService.delete(id);
        if (TenantDataSourceConfigController.log.isInfoEnabled()) {
            TenantDataSourceConfigController.log.info("{} instance {} was deleted.",
                    TenantDataSourceConfig.class.getSimpleName(), id);
        }
        return ResultDTO.success();
    }
}
