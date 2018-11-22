package com.leadingsoft.bizfuse.cloud.saas.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leadingsoft.bizfuse.cloud.saas.dto.SyncBean;
import com.leadingsoft.bizfuse.cloud.saas.dto.TenantDataSourceSyncBean;
import com.leadingsoft.bizfuse.cloud.saas.server.model.LatestModification;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantDataSourceConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.TenantDataSourceConfigRepository;
import com.leadingsoft.bizfuse.cloud.saas.server.service.LatestModificationService;
import com.leadingsoft.bizfuse.cloud.saas.server.service.TenantDataSourceConfigService;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

import lombok.NonNull;

/**
 * TenantDataSourceConfigService 实现类
 */
@Service
@Transactional
public class TenantDataSourceConfigServiceImpl implements TenantDataSourceConfigService {

    @Autowired
    private TenantDataSourceConfigRepository tenantDataSourceConfigRepository;
    @Autowired
    private LatestModificationService latestModificationService;

    @Override
    @Transactional(readOnly = true)
    public TenantDataSourceConfig get(@NonNull final Long id) {
        final TenantDataSourceConfig model = this.tenantDataSourceConfigRepository.findOne(id);
        if (model == null) {
            throw new CustomRuntimeException("404", String.format("查找的资源[%s]不存在.", id));
        }
        return model;
    }

    @Override
    public TenantDataSourceConfig create(final TenantDataSourceConfig model) {
        this.latestModificationService.update(this.getModelName());
        return this.tenantDataSourceConfigRepository.save(model);
    }

    @Override
    public TenantDataSourceConfig update(final TenantDataSourceConfig model) {
        this.latestModificationService.update(this.getModelName());
        return this.tenantDataSourceConfigRepository.save(model);
    }

    @Override
    public void delete(@NonNull final Long id) {
        this.latestModificationService.update(this.getModelName());
        this.tenantDataSourceConfigRepository.delete(id);
    }

    private String getModelName() {
        return TenantDataSourceConfig.class.getSimpleName();
    }

    @Override
    public SyncBean<List<TenantDataSourceConfig>> getAllIfExistModification(final TenantDataSourceSyncBean bean) {
        final LatestModification modification = this.latestModificationService
                .getByModifiedTimeAfter(this.getModelName(), bean.getLastModifiedTime());
        if (modification == null) {
            return null;
        }
        final SyncBean<List<TenantDataSourceConfig>> syncBean = new SyncBean<>();
        syncBean.setData(this.tenantDataSourceConfigRepository.findByServerTypeAndTenantNoIn(bean.getServerType(),
                bean.getTenantNos()));
        syncBean.setLastModifiedTime(modification.getLastModifiedTime());
        return syncBean;
    }
}
