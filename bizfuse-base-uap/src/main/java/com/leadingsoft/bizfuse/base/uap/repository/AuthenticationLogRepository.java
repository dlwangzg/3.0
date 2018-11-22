package com.leadingsoft.bizfuse.base.uap.repository;

import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.base.uap.model.AuthenticationLog;

public interface AuthenticationLogRepository extends Repository<AuthenticationLog, Long> {

    AuthenticationLog save(AuthenticationLog log);

}
