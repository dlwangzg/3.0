package com.leadingsoft.bizfuse.cloud.saas.client.datasource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bizfuse.saas.datasources.default", ignoreUnknownFields = true)
@ConditionalOnProperty(name = "bizfuse.saas.dynamicDataSource.enabled", matchIfMissing = true, havingValue = "true")
public class DataSourceProperties {

    private String driverClassName;

    private String url;

    private String username;

    private String password;

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public void setDriverClassName(final String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
