package com.leadingsoft.bizfuse.common.webauth.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.leadingsoft.bizfuse.common.webauth.access.CurrentUserArgumentResolver;
import com.leadingsoft.bizfuse.common.webauth.client.AuthRestTemplate;
import com.leadingsoft.bizfuse.common.webauth.util.SecurityUtils;

@Configuration
@ComponentScan
public class BizfuseWebAuthConfigurer extends WebMvcConfigurerAdapter {

    @Bean
    public CurrentUserArgumentResolver currentUserArgumentResolver() {
        return new CurrentUserArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(this.currentUserArgumentResolver());
    }

    @Bean
    public AuthRestTemplate authRestTemplate() {
        return new AuthRestTemplate();
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            final String loginUser = SecurityUtils.getCurrentUserLogin();
            return loginUser != null ? loginUser : "";
        };
    }
}
