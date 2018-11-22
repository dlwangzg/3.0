package com.leadingsoft.bizfuse.cloud.saas.client.loadbalance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.stereotype.Component;

import com.netflix.loadbalancer.Server;

@Component
public class TenantLoadbalancerClient {

    @Autowired
    private CachingSpringLoadBalancerFactory cachingSpringLoadBalancerFactory;

    public Server choose(final String serviceId) {
        return this.cachingSpringLoadBalancerFactory.create(serviceId).getLoadBalancer().chooseServer(serviceId);
    }
}
