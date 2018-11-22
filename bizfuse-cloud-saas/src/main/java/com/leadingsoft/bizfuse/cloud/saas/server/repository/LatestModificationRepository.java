package com.leadingsoft.bizfuse.cloud.saas.server.repository;

import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.cloud.saas.server.model.LatestModification;

public interface LatestModificationRepository extends Repository<LatestModification, Long> {

    LatestModification findByModelAndLastModifiedTimeGreaterThan(String model, long time);

    LatestModification findByModel(String model);

    LatestModification save(LatestModification modification);
}
