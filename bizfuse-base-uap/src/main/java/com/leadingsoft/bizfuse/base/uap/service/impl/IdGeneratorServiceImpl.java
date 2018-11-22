package com.leadingsoft.bizfuse.base.uap.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leadingsoft.bizfuse.base.uap.service.IdGeneratorService;
import com.leadingsoft.bizfuse.common.web.utils.id.BaseIdGenerator;

@Service
public class IdGeneratorServiceImpl implements IdGeneratorService {

    @Autowired
    private BaseIdGenerator userNoGenerator;

    @Override
    public String generateUserNo() {
        return this.userNoGenerator.generateCode();
    }

}
