package com.leadingsoft.bizfuse.cloud.saas.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.leadingsoft.bizfuse.cloud.saas.tenant.SecurityTenantUtils;
import com.leadingsoft.bizfuse.cloud.saas.tenant.SecurityTenantUtils.SimpleTenantContext;
import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContext;
import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContextHolder;
import com.leadingsoft.bizfuse.cloud.saas.utils.AntPathRequestMatcher;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityTenantCheckFilter extends AbstractTenantContextFilter {

    private List<AntPathRequestMatcher> matchers;

    public SecurityTenantCheckFilter(final List<String> whiteUrlPatterns) {
        if (whiteUrlPatterns == null) {
            this.matchers = new ArrayList<>();
        } else {
            this.matchers = whiteUrlPatterns.stream().filter(StringUtils::isNoneBlank)
                    .map(pattern -> new AntPathRequestMatcher(pattern))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public TenantContext getTenantContext(final ServletRequest request) throws TenantAuthenticationException {

        if (this.isWhiteUrl((HttpServletRequest) request)) {
            // 白名单列表里的请求，随机分配一个租户
            final String tenantNo = TenantContextHolder.getContext().getTenantNo();
            if (StringUtils.isNotBlank(tenantNo)) {
                return null;
            }
            return SecurityTenantUtils.getTenantContext();
        }
        final Collection<String> securityTenants = SecurityTenantUtils.getSecurityTenants();
        if (securityTenants.isEmpty()) {
            SecurityTenantCheckFilter.log.info("{} 访问无登录租户信息", ((HttpServletRequest) request).getRequestURI());
            return null;
        }
        String tenantNo = TenantContextHolder.getContext().getTenantNo();
        if (StringUtils.isNotBlank(tenantNo)) {
            // 校验租户
            if (!securityTenants.contains(tenantNo)) {
                if (securityTenants.size() == 1) { // 修正租户
                    final String securityTenant = securityTenants.iterator().next();
                    SecurityTenantCheckFilter.log.warn("登录用户的所属租户 {} 中不包含Request中的租户 [{}]，强制改为所属租户",
                            securityTenant, tenantNo);
                    SecurityTenantUtils.setSecurityTenant(tenantNo);
                    return new SimpleTenantContext(securityTenant);
                } else {
                    SecurityTenantCheckFilter.log.warn("登录用户的所属租户 {} 中不包含Request中的租户 [{}]，无权访问",
                            JsonUtils.pojoToJson(securityTenants), tenantNo);
                    throw new TenantAuthenticationException("租户不合法，无权访问");
                }
            }
            SecurityTenantUtils.setSecurityTenant(tenantNo);
            return null;
        }
        if (securityTenants.size() == 1) { // 单租户用户，采用默认租户策略
            tenantNo = securityTenants.iterator().next();
            if (SecurityTenantCheckFilter.log.isDebugEnabled()) {
                SecurityTenantCheckFilter.log.debug("单租户用户，采用默认租户策略： {}", tenantNo);
            }
            SecurityTenantUtils.setSecurityTenant(tenantNo);
            return new SimpleTenantContext(tenantNo);
        }
        SecurityTenantCheckFilter.log.warn("登录用户的所属租户 {} 为多个，无法确认当前租户，无权访问",
                JsonUtils.pojoToJson(securityTenants));
        throw new TenantAuthenticationException("租户不合法，无权访问");
    }

    private boolean isWhiteUrl(final HttpServletRequest request) {
        if (request.getRequestURI().startsWith("/saas/")) {
            return true;
        }
        return this.matchers.stream().anyMatch(matcher -> matcher.matches(request));
    }
}
