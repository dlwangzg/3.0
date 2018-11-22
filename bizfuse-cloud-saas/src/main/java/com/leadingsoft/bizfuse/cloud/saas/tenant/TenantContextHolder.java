package com.leadingsoft.bizfuse.cloud.saas.tenant;

public class TenantContextHolder {

    private static ThreadLocal<TenantContext> threadLocal = new ThreadLocal<>();

    public static final TenantContext EMPTY_CONTEXT = () -> null;

    public static TenantContext getContext() {
        final TenantContext context = TenantContextHolder.threadLocal.get();
        return context != null ? context : TenantContextHolder.EMPTY_CONTEXT;
    }

    public static String getTenantNo() {
        return TenantContextHolder.getContext().getTenantNo();
    }

    public static void set(final TenantContext context) {
        TenantContextHolder.threadLocal.set(context);
    }
}
