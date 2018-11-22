package com.leadingsoft.bizfuse.base.filestorage.controller;

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
import com.leadingsoft.bizfuse.base.filestorage.convertor.StorageRecordConvertor;
import com.leadingsoft.bizfuse.base.filestorage.dto.StorageRecordDTO;
import com.leadingsoft.bizfuse.base.filestorage.model.StorageRecord;
import com.leadingsoft.bizfuse.base.filestorage.repository.StorageRecordRepository;
import com.leadingsoft.bizfuse.base.filestorage.service.StorageRecordService;
import com.leadingsoft.bizfuse.common.web.dto.result.PageResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * StorageRecord的管理接口
 *
 * @author auto
 */
@Slf4j
@RestController
@RequestMapping("/w/storageRecords")
public class StorageRecordController {
    @Autowired
    private StorageRecordService storageRecordService;
    @Autowired
    private StorageRecordRepository storageRecordRepository;
    @Autowired
    private StorageRecordConvertor storageRecordConvertor;

    /**
     * 取得分页数据
     *
     * @param pageable 分页+排序参数
     * @return 分页数据
     */
    @Timed
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PageResultDTO<StorageRecordDTO> page(final Pageable pageable) {
        final Page<StorageRecord> models = this.storageRecordRepository.findAll(pageable);
        return this.storageRecordConvertor.toResultDTO(models);
    }

    /**
     * 取得详细数据
     *
     * @param id 资源ID
     * @return 资源详细
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<StorageRecordDTO> get(@PathVariable final String id) {
        final StorageRecord model = this.storageRecordService.getStorageRecord(id);
        return this.storageRecordConvertor.toResultDTO(model);
    }

    /**
     * 新建操作
     *
     * @param storageRecordDTO 新建资源的DTO
     * @return 新建资源
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<StorageRecordDTO> create(@RequestBody @Valid final StorageRecordDTO storageRecordDTO) {
        final StorageRecord model = this.storageRecordConvertor.toModel(storageRecordDTO);
        this.storageRecordService.createStorageRecord(model);
        if (StorageRecordController.log.isInfoEnabled()) {
            StorageRecordController.log.info("{} instance {} was created.", StorageRecord.class.getSimpleName(),
                    model.getId());
        }
        return this.storageRecordConvertor.toResultDTO(model);
    }

    /**
     * 更新操作
     *
     * @param storageRecordDTO 更新资源的DTO
     * @return 更新后资源
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<StorageRecordDTO> update(@PathVariable final String id,
            @RequestBody @Valid final StorageRecordDTO storageRecordDTO) {
        storageRecordDTO.setId(id);
        final StorageRecord model = this.storageRecordConvertor.toModel(storageRecordDTO);
        this.storageRecordService.updateStorageRecord(model);
        if (StorageRecordController.log.isInfoEnabled()) {
            StorageRecordController.log.info("{} instance {} was updated.", StorageRecord.class.getSimpleName(),
                    model.getId());
        }
        return this.storageRecordConvertor.toResultDTO(model);
    }

    /**
     * 删除操作
     *
     * @param id 资源ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultDTO<Void> delete(@PathVariable final String id) {
        this.storageRecordService.deleteStorageRecord(id);
        if (StorageRecordController.log.isInfoEnabled()) {
            StorageRecordController.log.info("{} instance {} was deleted.", StorageRecord.class.getSimpleName(), id);
        }
        return ResultDTO.success();
    }
}
