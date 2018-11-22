package com.leadingsoft.bizfuse.cloud.saas.filter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.leadingsoft.bizfuse.cloud.saas.tenant.Constants;
import com.leadingsoft.bizfuse.cloud.saas.tenant.SecurityTenantUtils.SimpleTenantContext;
import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContext;
import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContextHolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestHeaderTenantContextFilter extends AbstractTenantContextFilter {

    @Override
    public TenantContext getTenantContext(final ServletRequest request) throws TenantAuthenticationException {
        TenantContextHolder.set(TenantContextHolder.EMPTY_CONTEXT); // 清空线程内租户信息
        // 从请求头获取租户信息
        final HttpServletRequest req = (HttpServletRequest) request;
        final String tenantNo = req.getHeader(Constants.TENANT_HEADER);
        if (tenantNo == null) {
            if (RequestHeaderTenantContextFilter.log.isDebugEnabled()) {
                RequestHeaderTenantContextFilter.log.debug("URL {} 请求Headers无租户 [Tenant] 信息", req.getRequestURI());
            }
            return null;
        }
        return new SimpleTenantContext(tenantNo);
    }
}
