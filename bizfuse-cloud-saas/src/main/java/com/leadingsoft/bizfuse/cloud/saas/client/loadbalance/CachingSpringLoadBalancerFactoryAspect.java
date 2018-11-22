package com.leadingsoft.bizfuse.cloud.saas.client.loadbalance;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.stereotype.Component;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.LoadBalancerContext;

@Aspect
@Component
public class CachingSpringLoadBalancerFactoryAspect {

    @Autowired
    private SpringClientFactory factory;

    @Pointcut("execution(public * org.springframework.cloud.netflix.feign.ribbon.CachingSpringLoadBalancerFactory.create*(..))")
    public void create() {
    }

    @Around("create()")
    public Object processTx(final ProceedingJoinPoint jp) throws Throwable {
        final Object loadBalancer = jp.proceed();
        if (loadBalancer instanceof LoadBalancerContext) {
            final LoadBalancerContext context = (LoadBalancerContext) loadBalancer;
            final DynamicTenantServerListLoadBalancer tenantLoadBalancer =
                    (DynamicTenantServerListLoadBalancer) context.getLoadBalancer();
            if (tenantLoadBalancer.isInitiated()) {
                return loadBalancer;
            }
            final String serviceId = (String) jp.getArgs()[0];
            tenantLoadBalancer.setServiceId(serviceId);
            final IClientConfig clientConfig = this.factory.getClientConfig(serviceId);
            tenantLoadBalancer.initWithNiwsConfig(clientConfig);
        }
        return loadBalancer;
    }
}
