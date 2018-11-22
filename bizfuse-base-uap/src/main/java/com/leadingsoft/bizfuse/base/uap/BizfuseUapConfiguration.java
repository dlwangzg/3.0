package com.leadingsoft.bizfuse.base.uap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.leadingsoft.bizfuse.common.web.utils.encode.BCryptPasswordEncoder;
import com.leadingsoft.bizfuse.common.web.utils.encode.PasswordEncoder;
import com.leadingsoft.bizfuse.common.web.utils.id.BaseIdGenerator;
import com.leadingsoft.bizfuse.common.web.utils.id.DefaultIdGenerator;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class BizfuseUapConfiguration {

    //用户前缀
    private static final String USER_NO_PREFIX = "U";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Value("${cluster.siteId}")
    private int clusterSiteId;

    @Bean
    public BaseIdGenerator userNoGenerator() {
        return new DefaultIdGenerator(this.clusterSiteId, BizfuseUapConfiguration.USER_NO_PREFIX);
    }
}
