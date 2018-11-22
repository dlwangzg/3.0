package com.leadingsoft.bizfuse.cloud.saas.configuration.feign;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContext;
import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContextHolder;

public final class DelegatingSecurityContextRunnable implements Runnable {

    private final Runnable delegate;

    private final SecurityContext delegateSecurityContext;

    private final TenantContext tenantContext;

    private SecurityContext originalSecurityContext;
    private TenantContext originalTenantContext;

    public DelegatingSecurityContextRunnable(final Runnable delegate,
            final SecurityContext securityContext, final TenantContext tenantContext) {
        Assert.notNull(delegate, "delegate cannot be null");
        Assert.notNull(securityContext, "securityContext cannot be null");
        this.delegate = delegate;
        this.delegateSecurityContext = securityContext;
        this.tenantContext = tenantContext;
    }

    public DelegatingSecurityContextRunnable(final Runnable delegate) {
        this(delegate, SecurityContextHolder.getContext(), TenantContextHolder.getContext());
    }

    @Override
    public void run() {
        this.originalSecurityContext = SecurityContextHolder.getContext();
        this.originalTenantContext = TenantContextHolder.getContext();
        try {
            SecurityContextHolder.setContext(this.delegateSecurityContext);
            TenantContextHolder.set(this.tenantContext);
            this.delegate.run();
        } finally {
            final SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
            if (emptyContext.equals(this.originalSecurityContext)) {
                SecurityContextHolder.clearContext();
            } else {
                SecurityContextHolder.setContext(this.originalSecurityContext);
            }
            TenantContextHolder.set(this.originalTenantContext);
            this.originalSecurityContext = null;
            this.originalTenantContext = null;
        }
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    /**
     * Factory method for creating a {@link DelegatingSecurityContextRunnable}.
     *
     * @param delegate the original {@link Runnable} that will be delegated to
     *        after establishing a {@link SecurityContext} on the
     *        {@link SecurityContextHolder}. Cannot have null.
     * @param securityContext the {@link SecurityContext} to establish before
     *        invoking the delegate {@link Runnable}. If null, the current
     *        {@link SecurityContext} from the {@link SecurityContextHolder}
     *        will be used.
     * @return
     */
    public static Runnable create(final Runnable delegate, final SecurityContext securityContext) {
        Assert.notNull(delegate, "delegate cannot be  null");
        return securityContext == null ? new DelegatingSecurityContextRunnable(delegate)
                : new DelegatingSecurityContextRunnable(delegate, securityContext, TenantContextHolder.getContext());
    }
}
