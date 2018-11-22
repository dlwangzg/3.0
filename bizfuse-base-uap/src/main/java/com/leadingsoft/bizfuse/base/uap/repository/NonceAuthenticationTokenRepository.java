package com.leadingsoft.bizfuse.base.uap.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.base.uap.model.NonceAuthenticationToken;

public interface NonceAuthenticationTokenRepository extends Repository<NonceAuthenticationToken, Long> {

    NonceAuthenticationToken save(NonceAuthenticationToken token);

    NonceAuthenticationToken findOne(Long id);

    List<NonceAuthenticationToken> findByNoAndDeviceId(String no, String deviceId);

    void delete(NonceAuthenticationToken token);

}
