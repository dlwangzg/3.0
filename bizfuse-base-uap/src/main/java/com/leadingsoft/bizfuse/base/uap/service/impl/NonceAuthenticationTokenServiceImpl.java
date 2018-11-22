package com.leadingsoft.bizfuse.base.uap.service.impl;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import com.leadingsoft.bizfuse.base.uap.model.NonceAuthenticationToken;
import com.leadingsoft.bizfuse.base.uap.model.User;
import com.leadingsoft.bizfuse.base.uap.repository.NonceAuthenticationTokenRepository;
import com.leadingsoft.bizfuse.base.uap.repository.UserRepository;
import com.leadingsoft.bizfuse.base.uap.service.NonceAuthenticationTokenService;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

@Service
@Transactional
public class NonceAuthenticationTokenServiceImpl implements NonceAuthenticationTokenService {

    private final long nonceTokenExpiredSeconds = 14 * 24 * 3600;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NonceAuthenticationTokenRepository nonceAuthenticationTokenRepository;

    @Override
    public User authenticate(final String nonceToken, final String... deviceInfos) {
        final Long tokenId = this.decodeTokenId(nonceToken);
        if (tokenId == null) {
            throw new CustomRuntimeException("401", "无效的认证CODE");
        }
        final NonceAuthenticationToken token = this.nonceAuthenticationTokenRepository.findOne(tokenId);
        if ((token == null) || token.isExpired()) {
        	 throw new CustomRuntimeException("401", "认证CODE已失效");
        }
        if (!this.deviceMatches(token, deviceInfos)) {
        	throw new CustomRuntimeException("401", "无效的认证CODE");
        }
        final User user = this.userRepository.findOneByNo(token.getNo());
        // 认证通过，删除旧的token
        this.nonceAuthenticationTokenRepository.delete(token);
        return user;
    }

    @Override
    public String createNonceToken(final NonceAuthenticationToken token) {
        if (!StringUtils.hasText(token.getDeviceId())
                || !StringUtils.hasText(token.getDeviceType())
                || !StringUtils.hasText(token.getOsType())
                || !StringUtils.hasText(token.getOsVersion())) {
            return null;
        }
        // 如果存在同一用户+设备ID相同的token，删除旧的记录
        final List<NonceAuthenticationToken> oldTokens =
                this.nonceAuthenticationTokenRepository.findByNoAndDeviceId(token.getNo(), token.getDeviceId());
        oldTokens.forEach(oldToken -> {
            this.nonceAuthenticationTokenRepository.delete(oldToken);
        });
        final long expiredTime = System.currentTimeMillis() + (this.nonceTokenExpiredSeconds * 1000);
        token.setExpiredDate(new Date(expiredTime));
        this.nonceAuthenticationTokenRepository.save(token);
        final String nonceToken = this.encodeTokenId(token.getId());
        return nonceToken;
    }

    @Override
    public void deleteNonceToken(final String nonceToken) {
        final Long tokenId = this.decodeTokenId(nonceToken);
        if (tokenId == null) {
            return;
        }
        final NonceAuthenticationToken token = this.nonceAuthenticationTokenRepository.findOne(tokenId);
        if (token != null) {
            this.nonceAuthenticationTokenRepository.delete(token);
        }
    }

    private Long decodeTokenId(final String nonceToken) {
        String tokenStr = null;
        try {
            final byte[] originalTokenId = Base64Utils.decodeFromString(nonceToken);
            tokenStr = new String(originalTokenId, Charset.forName("UTF-8"));
        } catch (final Exception e) {
            return null;
        }
        if (tokenStr.length() < 17) {
            return null;
        }
        final String tokenIdStr = tokenStr.substring(6, tokenStr.length() - 10);
        try {
            return Long.parseLong(tokenIdStr);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    /**
     * 6位随机数 + ID + 时间戳到秒（10位）
     *
     * @param id
     * @return
     */
    private String encodeTokenId(final long id) {
        final StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append((int) (Math.random() * 10));
        }
        code.append(id);
        final String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        if (timeStamp.length() != 10) {
            throw new RuntimeException("服务器时钟时间错误");
        }
        code.append(timeStamp);
        final String nonceToken = Base64Utils.encodeToString(code.toString().getBytes());
        return nonceToken;
    }

    private boolean deviceMatches(final NonceAuthenticationToken token, final String... deviceInfos) {
        final String[] storeInfos = new String[] {
                token.getDeviceId(), token.getDeviceType(), token.getOsType(),
                token.getOsVersion(), token.getSoftwareType(), token.getSoftwareVersion()
        };
        if (deviceInfos.length != storeInfos.length) {
            return false;
        }
        Arrays.sort(deviceInfos);
        Arrays.sort(storeInfos);
        for (int i = 0; i < storeInfos.length; i++) {
            if (!storeInfos[i].equals(deviceInfos[i])) {
                return false;
            }
        }
        return true;
    }
}
