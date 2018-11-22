package com.leadingsoft.bizfuse.cloud.saas.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leadingsoft.bizfuse.cloud.saas.dto.SyncBean;
import com.leadingsoft.bizfuse.cloud.saas.dto.TenantServerRelationBean;
import com.leadingsoft.bizfuse.cloud.saas.server.model.LatestModification;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantServerConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.TenantServerConfigRepository;
import com.leadingsoft.bizfuse.cloud.saas.server.service.LatestModificationService;
import com.leadingsoft.bizfuse.cloud.saas.server.service.TenantServerConfigService;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

import lombok.NonNull;

/**
 * TenantServerConfigService 实现类
 */
@Service
@Transactional
public class TenantServerConfigServiceImpl implements TenantServerConfigService {

    @Autowired
    private TenantServerConfigRepository tenantServerConfigRepository;
    @Autowired
    private LatestModificationService latestModificationService;

    @Override
    @Transactional(readOnly = true)
    public TenantServerConfig get(@NonNull final Long id) {
        final TenantServerConfig model = this.tenantServerConfigRepository.findOne(id);
        if (model == null) {
            throw new CustomRuntimeException("404", String.format("查找的资源[%s]不存在.", id));
        }
        return model;
    }

    @Override
    public TenantServerConfig create(final TenantServerConfig model) {
        this.latestModificationService.update(TenantServerConfig.class.getSimpleName());
        return this.tenantServerConfigRepository.save(model);
    }

    @Override
    public TenantServerConfig update(final TenantServerConfig model) {
        this.latestModificationService.update(TenantServerConfig.class.getSimpleName());
        return this.tenantServerConfigRepository.save(model);
    }

    @Override
    public void delete(@NonNull final Long id) {
        this.latestModificationService.update(TenantServerConfig.class.getSimpleName());
        this.tenantServerConfigRepository.delete(id);
    }

    @Override
    public SyncBean<List<TenantServerRelationBean>> getAllIfExistModification(final long lastModifiedTime) {
        final LatestModification modification = this.latestModificationService
                .getByModifiedTimeAfter(TenantServerConfig.class.getSimpleName(), lastModifiedTime);
        if (modification == null) {
            return null;
        }
        final SyncBean<List<TenantServerRelationBean>> syncBean = new SyncBean<>();
        syncBean.setData(this.tenantServerConfigRepository.findAll());
        syncBean.setLastModifiedTime(modification.getLastModifiedTime());
        return syncBean;
    }
}
