package com.leadingsoft.bizfuse.cloud.saas.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import com.leadingsoft.bizfuse.cloud.saas.configuration.feign.FeignHystrixSecurityAutoConfiguration;
import com.leadingsoft.bizfuse.cloud.saas.configuration.feign.FeignTenantRequestInterceptor;
import com.leadingsoft.bizfuse.cloud.saas.configuration.ribbon.TenantEurekaRibbonClientConfiguration;
import com.leadingsoft.bizfuse.cloud.saas.filter.RequestHeaderTenantContextFilter;
import com.leadingsoft.bizfuse.cloud.saas.filter.SecurityTenantCheckFilter;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@RibbonClients(defaultConfiguration = TenantEurekaRibbonClientConfiguration.class)
@Import(FeignHystrixSecurityAutoConfiguration.class)
public class BizfuseSaasConfigration {
    @Value("${tenant.context.filter.order:2147483647}")
    private Integer tenantFilterOrder;

    @Bean
    @ConditionalOnMissingBean(name = "tenantContextFilter")
    public FilterRegistrationBean tenantContextFilter(final SaaSProperties SaaSProperties) {
        final SecurityTenantCheckFilter filter =
                new SecurityTenantCheckFilter(SaaSProperties.getTenantCheckWhitePatterns());
        final FilterRegistrationBean fBean = new FilterRegistrationBean();
        fBean.setFilter(filter);
        fBean.setOrder(this.tenantFilterOrder);
        fBean.addUrlPatterns("/*");
        return fBean;
    }

    @Bean
    @ConditionalOnMissingBean(name = "requestHeaderTenantContextFilter")
    public FilterRegistrationBean requestHeaderTenantContextFilter() {
        final RequestHeaderTenantContextFilter filter = new RequestHeaderTenantContextFilter();
        final FilterRegistrationBean fBean = new FilterRegistrationBean();
        fBean.setFilter(filter);
        fBean.setOrder(1);
        fBean.addUrlPatterns("/*");
        return fBean;
    }

    @Bean
    public FeignTenantRequestInterceptor feignTenantRequestInterceptor() {
        return new FeignTenantRequestInterceptor();
    }
}
