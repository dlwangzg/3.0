package com.leadingsoft.bizfuse.cloud.saas.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantUserConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.TenantUserConfigRepository;
import com.leadingsoft.bizfuse.cloud.saas.server.service.LatestModificationService;
import com.leadingsoft.bizfuse.cloud.saas.server.service.TenantUserConfigService;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

import lombok.NonNull;

/**
 * TenantUserConfigService 实现类
 */
@Service
@Transactional
public class TenantUserConfigServiceImpl implements TenantUserConfigService {

    @Autowired
    private TenantUserConfigRepository tenantUserConfigRepository;
    @Autowired
    private LatestModificationService latestModificationService;

    @Override
    @Transactional(readOnly = true)
    public TenantUserConfig get(@NonNull final Long id) {
        final TenantUserConfig model = this.tenantUserConfigRepository.findOne(id);
        if (model == null) {
            throw new CustomRuntimeException("404", String.format("查找的资源[%s]不存在.", id));
        }
        return model;
    }

    @Override
    public TenantUserConfig create(final TenantUserConfig model) {
        this.latestModificationService.update(TenantUserConfig.class.getSimpleName());
        return this.tenantUserConfigRepository.save(model);
    }

    @Override
    public TenantUserConfig update(final TenantUserConfig model) {
        this.latestModificationService.update(TenantUserConfig.class.getSimpleName());
        return this.tenantUserConfigRepository.save(model);
    }

    @Override
    public void delete(@NonNull final Long id) {
        this.latestModificationService.update(TenantUserConfig.class.getSimpleName());
        this.tenantUserConfigRepository.delete(id);
    }
}
