package com.leadingsoft.bizfuse.cloud.saas.server.controller;

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
import com.leadingsoft.bizfuse.cloud.saas.dto.TenantUserConfigDTO;
import com.leadingsoft.bizfuse.cloud.saas.server.convertor.TenantUserConfigConvertor;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantUserConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.TenantUserConfigRepository;
import com.leadingsoft.bizfuse.cloud.saas.server.service.TenantUserConfigService;
import com.leadingsoft.bizfuse.common.web.dto.result.PageResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * TenantUserConfig的管理接口
 *
 * @author auto
 */
@Slf4j
@RestController
@RequestMapping("/saas/tenantUserConfigs")
@Api(tags = {"TenantUserConfig管理API" })
public class TenantUserConfigController {
    @Autowired
    private TenantUserConfigService tenantUserConfigService;
    @Autowired
    private TenantUserConfigRepository tenantUserConfigRepository;
    @Autowired
    private TenantUserConfigConvertor tenantUserConfigConvertor;

    /**
     * 获取分页数据
     *
     * @param pageable 分页+排序参数
     * @return 分页数据
     */
    @Timed
    @ApiOperation(value = "获取分页数据", notes = "")
    @RequestMapping(value = "/s", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResultDTO<TenantUserConfigDTO> search(final Pageable pageable) {
        final Page<TenantUserConfig> models = this.tenantUserConfigRepository.findAll(pageable);
        return this.tenantUserConfigConvertor.toResultDTO(models);
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
    public ResultDTO<TenantUserConfigDTO> get(@PathVariable final Long id) {
        final TenantUserConfig model = this.tenantUserConfigService.get(id);
        return this.tenantUserConfigConvertor.toResultDTO(model);
    }

    /**
     * 新建操作
     *
     * @param tenantUserConfigDTO 新建资源的DTO
     * @return 新建资源
     */
    @Timed
    @ApiOperation(value = "新建操作", notes = "")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<TenantUserConfigDTO> create(@RequestBody @Valid final TenantUserConfigDTO tenantUserConfigDTO) {
        final TenantUserConfig model = this.tenantUserConfigConvertor.toModel(tenantUserConfigDTO);
        this.tenantUserConfigService.create(model);
        if (TenantUserConfigController.log.isInfoEnabled()) {
            TenantUserConfigController.log.info("{} instance {} was created.", TenantUserConfig.class.getSimpleName(),
                    model.getId());
        }
        return this.tenantUserConfigConvertor.toResultDTO(model);
    }

    /**
     * 更新操作
     *
     * @param id 更新资源的ID
     * @param tenantUserConfigDTO 更新资源的DTO
     * @return 更新后资源
     */
    @Timed
    @ApiOperation(value = "更新操作", notes = "")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<TenantUserConfigDTO> update(@PathVariable final Long id,
            @RequestBody @Valid final TenantUserConfigDTO tenantUserConfigDTO) {
        tenantUserConfigDTO.setId(id);
        final TenantUserConfig model = this.tenantUserConfigConvertor.toModel(tenantUserConfigDTO);
        this.tenantUserConfigService.update(model);
        if (TenantUserConfigController.log.isInfoEnabled()) {
            TenantUserConfigController.log.info("{} instance {} was updated.", TenantUserConfig.class.getSimpleName(),
                    model.getId());
        }
        return this.tenantUserConfigConvertor.toResultDTO(model);
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
        this.tenantUserConfigService.delete(id);
        if (TenantUserConfigController.log.isInfoEnabled()) {
            TenantUserConfigController.log.info("{} instance {} was deleted.", TenantUserConfig.class.getSimpleName(),
                    id);
        }
        return ResultDTO.success();
    }
}
