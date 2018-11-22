package com.leadingsoft.bizfuse.cloud.saas.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leadingsoft.bizfuse.cloud.saas.server.model.LatestModification;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.LatestModificationRepository;
import com.leadingsoft.bizfuse.cloud.saas.server.service.LatestModificationService;

@Service
public class LatestModificationServiceImpl implements LatestModificationService {

    @Autowired
    private LatestModificationRepository latestModificationRepository;

    @Override
    public void update(final String model) {
        LatestModification modification = this.latestModificationRepository.findByModel(model);
        if (modification == null) {
            modification = new LatestModification();
            modification.setModel(model);
        }
        modification.markModified();
        this.latestModificationRepository.save(modification);
    }

    @Override
    public LatestModification getByModifiedTimeAfter(final String model, final long time) {
        return this.latestModificationRepository.findByModelAndLastModifiedTimeGreaterThan(model, time);
    }

}
