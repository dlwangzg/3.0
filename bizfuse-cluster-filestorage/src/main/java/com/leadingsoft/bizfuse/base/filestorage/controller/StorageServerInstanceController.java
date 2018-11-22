package com.leadingsoft.bizfuse.base.filestorage.controller;

import com.leadingsoft.bizfuse.base.filestorage.model.StorageServerInstance;
import com.leadingsoft.bizfuse.base.filestorage.repository.StorageServerInstanceRepository;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 尚晓琼 on 2017/8/21.
 */
@RestController
@Slf4j
public class StorageServerInstanceController {
    @Autowired
    StorageServerInstanceRepository storageServerInstanceRepository;

    @RequestMapping(value = "/server/{id}", method = RequestMethod.DELETE)
    public ResultDTO<Void> deleteServerInstance(@PathVariable final String id) {
        StorageServerInstance serverInstance=this.storageServerInstanceRepository.findOne(id);
        if(serverInstance==null){
            throw new CustomRuntimeException("404","id不存在");
        }
        this.storageServerInstanceRepository.delete(id);
        if (StorageServerInstanceController.log.isInfoEnabled()) {
            StorageServerInstanceController.log.info("serverInstance [id:{}] was deleted.",id);
        }
        return ResultDTO.success();
    }
}

