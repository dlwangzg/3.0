package com.leadingsoft.bizfuse.cloud.saas.server.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leadingsoft.bizfuse.cloud.saas.dto.SyncBean;
import com.leadingsoft.bizfuse.cloud.saas.server.model.LatestModification;
import com.leadingsoft.bizfuse.cloud.saas.server.model.ServerInstance;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.ServerInstanceRepository;
import com.leadingsoft.bizfuse.cloud.saas.server.service.LatestModificationService;
import com.leadingsoft.bizfuse.cloud.saas.server.service.ServerInstanceService;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

import lombok.NonNull;

/**
 * ServerInstanceService 实现类
 */
@Service
@Transactional
public class ServerInstanceServiceImpl implements ServerInstanceService {

    @Autowired
    private ServerInstanceRepository serverInstanceRepository;
    @Autowired
    private LatestModificationService latestModificationService;

    @Override
    @Transactional(readOnly = true)
    public ServerInstance get(@NonNull final Long id) {
        final ServerInstance model = this.serverInstanceRepository.findOne(id);
        if (model == null) {
            throw new CustomRuntimeException("404", String.format("查找的资源[%s]不存在.", id));
        }
        return model;
    }

    @Override
    public ServerInstance create(final ServerInstance model) {
        final ServerInstance serverInstance =
                this.serverInstanceRepository.findByInternalIPAndPort(model.getInternalIP(), model.getPort());
        if (serverInstance != null) {
            // 已经注册了服务
            return serverInstance;
        }
        this.latestModificationService.update(ServerInstance.class.getSimpleName());
        return this.serverInstanceRepository.save(model);
    }

    @Override
    public ServerInstance update(final ServerInstance model) {
        this.latestModificationService.update(ServerInstance.class.getSimpleName());
        return this.serverInstanceRepository.save(model);
    }

    @Override
    public void delete(@NonNull final Long id) {
        this.latestModificationService.update(ServerInstance.class.getSimpleName());
        this.serverInstanceRepository.delete(id);
    }

    @Override
    public SyncBean<List<ServerInstance>> getAllIfExistModification(final long lastModifiedTime) {
        final LatestModification modification = this.latestModificationService
                .getByModifiedTimeAfter(ServerInstance.class.getSimpleName(), lastModifiedTime);
        if (modification == null) {
            return null;
        }
        final SyncBean<List<ServerInstance>> syncBean = new SyncBean<>();
        syncBean.setData(this.serverInstanceRepository.findAll());
        syncBean.setLastModifiedTime(modification.getLastModifiedTime());
        return syncBean;
    }
}
