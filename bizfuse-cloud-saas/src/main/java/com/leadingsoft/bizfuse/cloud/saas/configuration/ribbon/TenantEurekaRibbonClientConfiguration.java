package com.leadingsoft.bizfuse.cloud.saas.configuration.ribbon;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.leadingsoft.bizfuse.cloud.saas.client.loadbalance.DynamicTenantServerListLoadBalancer;

@Configuration
public class TenantEurekaRibbonClientConfiguration {

    public TenantEurekaRibbonClientConfiguration() {
    }

    @Bean
    @ConditionalOnProperty(name = "bizfuse.saas.loadbalance.enabled", matchIfMissing = true, havingValue = "true")
    public DynamicTenantServerListLoadBalancer dynamicServerListLoadBalancer() {
        final DynamicTenantServerListLoadBalancer balancer = new DynamicTenantServerListLoadBalancer();
        return balancer;
    }
}
