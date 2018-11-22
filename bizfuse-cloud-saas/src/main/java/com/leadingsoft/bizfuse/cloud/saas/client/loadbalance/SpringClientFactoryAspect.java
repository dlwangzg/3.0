package com.leadingsoft.bizfuse.cloud.saas.client.loadbalance;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

// @Aspect
// @Component
public class SpringClientFactoryAspect {

    @Pointcut("execution(public * org.springframework.cloud.netflix.ribbon.SpringClientFactory.getLoadBalancer*(..))")
    public void create() {
    }

    @Around("create()")
    public Object processTx(final ProceedingJoinPoint jp) throws Throwable {
        final Object loadBalancer = jp.proceed();
        if (loadBalancer instanceof DynamicTenantServerListLoadBalancer) {
            final DynamicTenantServerListLoadBalancer tenantLoadBalancer =
                    (DynamicTenantServerListLoadBalancer) loadBalancer;
            if (tenantLoadBalancer.isInitiated()) {
                return loadBalancer;
            }
            final String serviceId = (String) jp.getArgs()[0];
            tenantLoadBalancer.setServiceId(serviceId);
            return loadBalancer;
        }
        return loadBalancer;
    }
}
