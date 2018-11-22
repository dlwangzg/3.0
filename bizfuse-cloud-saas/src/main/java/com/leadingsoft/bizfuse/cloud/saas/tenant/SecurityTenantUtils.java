package com.leadingsoft.bizfuse.cloud.saas.tenant;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityTenantUtils {

    private static Map<Class<?>, Method> tenantsGetMethods = new HashMap<>();
    private static Map<Class<?>, Method> tenantGetMethods = new HashMap<>();
    private static Map<Class<?>, Method> tenantSetMethods = new HashMap<>();

    private static Object[] emptyArgs = new Object[0];

    public static void setSecurityTenant(final String tenant) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ((auth == null) || (auth instanceof AnonymousAuthenticationToken)) {
            SecurityTenantUtils.log.debug("匿名用户无租户信息");
            return;
        }
        final Class<? extends Object> objClass = auth.getClass();
        Method setMethod = SecurityTenantUtils.tenantSetMethods.get(objClass);
        if (setMethod == null) {
            try {
                setMethod = objClass.getDeclaredMethod("setTenantNo", String.class);
                setMethod.setAccessible(true);
                SecurityTenantUtils.tenantSetMethods.put(objClass, setMethod);
            } catch (final Exception e) {
                SecurityTenantUtils.log.debug("AuthToken 获取[ setTenantNo ]方法发生异常", e);
                return;
            }
        }
        try {
            setMethod.invoke(auth, tenant);
        } catch (final Exception e) {
            SecurityTenantUtils.log.debug("反射调用[ setTenantNo ]方法发生异常", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Collection<String> getSecurityTenants() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ((auth == null) || (auth instanceof AnonymousAuthenticationToken)) {
            SecurityTenantUtils.log.debug("匿名用户无租户信息");
            return Collections.emptyList();
        }
        final Class<? extends Object> objClass = auth.getClass();
        Method getMethod = SecurityTenantUtils.tenantsGetMethods.get(objClass);
        if (getMethod == null) {
            try {
                final Class<?>[] params = null;
                getMethod = objClass.getDeclaredMethod("getTenantNos", params);
                getMethod.setAccessible(true);
                SecurityTenantUtils.tenantsGetMethods.put(objClass, getMethod);
            } catch (final Exception e) {
                SecurityTenantUtils.log.debug("AuthToken 获取[ getTenantNo ]方法发生异常", e);
                return Collections.emptyList();
            }
        }
        try {
            final Object tenants = getMethod.invoke(auth, SecurityTenantUtils.emptyArgs);
            if (tenants == null) {
                return Collections.emptyList();
            }
            return (Collection<String>) tenants;
        } catch (final Exception e) {
            SecurityTenantUtils.log.debug("反射调用[ setTenantNo ]方法发生异常", e);
            return Collections.emptyList();
        }
    }

    public static TenantContext getTenantContext() {
        final TenantContext context = TenantContextHolder.getContext();
        if (context.getTenantNo() != null) {
            return context;
        }
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        if (auth instanceof TenantContext) {
            return (TenantContext) auth;
        }
        return SecurityTenantUtils.getTenantContextByReflect(auth);
    }

    private static TenantContext getTenantContextByReflect(final Object auth) {
        if ((auth == null) || (auth instanceof AnonymousAuthenticationToken)) {
            SecurityTenantUtils.log.debug("匿名用户无租户信息");
            return null;
        }
        final Class<? extends Object> objClass = auth.getClass();
        Method getMethod = SecurityTenantUtils.tenantGetMethods.get(objClass);
        if (getMethod == null) {
            try {
                final Class<?>[] params = null;
                getMethod = objClass.getDeclaredMethod("getTenantNo", params);
                getMethod.setAccessible(true);
                SecurityTenantUtils.tenantGetMethods.put(objClass, getMethod);
            } catch (final Exception e) {
                SecurityTenantUtils.log.debug("AuthToken 获取[ getTenantNo ]方法发生异常", e);
                return null;
            }
        }
        try {

            final String tenantNo = (String) getMethod.invoke(auth, SecurityTenantUtils.emptyArgs);
            return new SimpleTenantContext(tenantNo);
        } catch (final Exception e) {
            SecurityTenantUtils.log.debug("反射调用[ getTenantNo ]方法发生异常", e);
        }
        return null;
    }

    public static class SimpleTenantContext implements TenantContext {

        private final String no;

        public SimpleTenantContext(final String no) {
            this.no = no;
        }

        @Override
        public String getTenantNo() {
            return this.no;
        }
    }
}
