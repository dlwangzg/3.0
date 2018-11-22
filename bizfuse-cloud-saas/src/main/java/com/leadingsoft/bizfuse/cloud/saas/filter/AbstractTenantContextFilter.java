package com.leadingsoft.bizfuse.cloud.saas.filter;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContext;
import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContextHolder;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultError;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;

public abstract class AbstractTenantContextFilter implements Filter {

    private final Set<String> ignoreKeywords = new HashSet<>();

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.ignoreKeywords.add(".css");
        this.ignoreKeywords.add(".js");
        this.ignoreKeywords.add(".png");
        this.ignoreKeywords.add(".jpg");
        this.ignoreKeywords.add(".jpeg");
        this.ignoreKeywords.add(".html");
    };

    @Override
    public void destroy() {
    };

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final String requestURI = ((HttpServletRequest) request).getRequestURI();
        if (this.ignoreKeywords.stream().anyMatch(key -> requestURI.endsWith(key))) {
            chain.doFilter(request, response);
            return;
        }
        TenantContext tenant;
        try {
            tenant = this.getTenantContext(request);
            if (tenant != null) {
                TenantContextHolder.set(tenant);
            }
            chain.doFilter(request, response);
        } catch (final TenantAuthenticationException e) {
            final ResultError error = new ResultError("403", e.getMessage(), null);
            final ResultDTO<?> rs = ResultDTO.failure(error);
            ((HttpServletResponse) response).setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            final Writer writer = response.getWriter();
            writer.write(JsonUtils.pojoToJson(rs));
            writer.flush();
            writer.close();
        }
    }

    protected abstract TenantContext getTenantContext(final ServletRequest request)
            throws TenantAuthenticationException;

    public static class TenantAuthenticationException extends Exception {

        private static final long serialVersionUID = 1L;

        public TenantAuthenticationException(final String msg) {
            super(msg);
        }
    }
}
