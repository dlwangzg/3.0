package com.leadingsoft.bizfuse.cloud.saas.configuration.feign;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.leadingsoft.bizfuse.cloud.saas.tenant.Constants;
import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContextHolder;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * Feign 客户端认证信息拦截器
 *
 * @author liuyg
 */
@Slf4j
public class FeignTenantRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(final RequestTemplate template) {

        final String tenantNo = TenantContextHolder.getContext().getTenantNo();
        if (tenantNo != null) { // 构造租户信息
            final Map<String, Collection<String>> header = template.headers();
            final Map<String, Collection<String>> newHeader = new HashMap<>(header);
            newHeader.put(Constants.TENANT_HEADER, Arrays.asList(tenantNo));
            template.headers(newHeader);
        } else {
            if (FeignTenantRequestInterceptor.log.isDebugEnabled()) {
                FeignTenantRequestInterceptor.log.debug("Feign Client 构造租户Header时，上下文无租户信息");
            }
        }
    }
}
