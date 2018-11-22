package com.leadingsoft.bizfuse.base.uap.service;

import com.leadingsoft.bizfuse.base.uap.model.NonceAuthenticationToken;
import com.leadingsoft.bizfuse.base.uap.model.User;

public interface NonceAuthenticationTokenService {

    User authenticate(String nonceToken, String... deviceInfo);

    String createNonceToken(NonceAuthenticationToken token);

    void deleteNonceToken(String nonceToken);
}
