package com.leadingsoft.bizfuse.common.webauth.access;

import org.springframework.data.domain.AuditorAware;

import com.leadingsoft.bizfuse.common.webauth.util.SecurityUtils;

/**
 * 默认的Model审计实现
 *
 * @author liuyg
 */
public class DefaultAuditorAwareImpl implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        final String userNo = SecurityUtils.getCurrentUserLogin();
        if (userNo != null) {
            return userNo;
        }
        return "";
    }

}
