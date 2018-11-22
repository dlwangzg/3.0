package com.leadingsoft.bizfuse.cloud.saas.client.datasource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContext;
import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * 租户动态路由数据源
 *
 * @author liuyg
 */
@Slf4j
@Primary
@Component("dataSource")
@ConditionalOnProperty(name = "bizfuse.saas.dynamicDataSource.enabled", matchIfMissing = true, havingValue = "true")
public class TenantDynamicRoutingDataSource extends AbstractRoutingDataSource {

    @Autowired
    private DataSourceProperties defaultDS;
    private TenantDataSourceConfigClient dataSourceClient;
    private DataSource defaultDataSource;
    private final Map<Object, DataSource> unhitDataSources = new HashMap<>();

    @Override
    protected DataSource determineTargetDataSource() {
        final DataSource ds = super.determineTargetDataSource();
        if (!this.isDefaultDS(ds)) {
            return ds;
        }

        final Object lookupKey = this.determineCurrentLookupKey();
        if (lookupKey == null) {
            TenantDynamicRoutingDataSource.log.debug("请求无租户信息，返回默认数据源");
            return ds;
        }
        DataSource dataSource = this.unhitDataSources.get(lookupKey);
        if ((dataSource == null) && (this.dataSourceClient != null)) {
            if (this.unhitDataSources.size() > 500) {
                this.unhitDataSources.clear();
            }
            dataSource = this.dataSourceClient.syncTenantDataSource((String) lookupKey);
            if (dataSource != null) {
                this.unhitDataSources.put((String) lookupKey, dataSource);
            }
        }
        return dataSource != null ? dataSource : ds;
    }
    
    public void updateUnhitDataSources(final Map<String, DataSource> dataSources) {
    	dataSources.entrySet().forEach(entry -> unhitDataSources.put(entry.getKey(), entry.getValue()));
        this.printDataSources(unhitDataSources);
    }
    
    public void reloadDataSources(final Map<String, DataSource> dataSources) throws Exception {
        final Field targetDS = AbstractRoutingDataSource.class.getDeclaredField("resolvedDataSources");
        targetDS.setAccessible(true);
        @SuppressWarnings("unchecked")
        final Map<Object, DataSource> resolvedDataSources = (Map<Object, DataSource>) targetDS.get(this);
        resolvedDataSources.clear();
        resolvedDataSources.putAll(dataSources);
        this.printDataSources(resolvedDataSources);
    }

    public void updateDataSources(final Map<String, DataSource> dataSources) throws Exception {
        final Field targetDS = AbstractRoutingDataSource.class.getDeclaredField("resolvedDataSources");
        targetDS.setAccessible(true);
        @SuppressWarnings("unchecked")
        final Map<Object, DataSource> resolvedDataSources = (Map<Object, DataSource>) targetDS.get(this);
        dataSources.entrySet().forEach(entry -> resolvedDataSources.put(entry.getKey(), entry.getValue()));
        this.printDataSources(resolvedDataSources);
    }

    public void removeDataSource(final List<String> lookupKeys) throws Exception {
        final Field targetDS = AbstractRoutingDataSource.class.getDeclaredField("resolvedDataSources");
        targetDS.setAccessible(true);
        @SuppressWarnings("unchecked")
        final Map<Object, DataSource> resolvedDataSources = (Map<Object, DataSource>) targetDS.get(this);
        lookupKeys.stream().forEach(lookupKey -> resolvedDataSources.remove(lookupKey));
        this.printDataSources(resolvedDataSources);
    }
    
    public List<String> getUnhitDataSourceTenants() {
    	return this.unhitDataSources.keySet().stream().map(v -> (String)v).collect(Collectors.toList());
    }

    @Override
    protected Object determineCurrentLookupKey() {
        final TenantContext tenant = TenantContextHolder.getContext();
        final String key = tenant != null ? tenant.getTenantNo() : null;
        if (TenantDynamicRoutingDataSource.log.isDebugEnabled()) {
            TenantDynamicRoutingDataSource.log.debug("数据源LookupKey: {}", key);
        }
        return key;
    }

    @Override
    public void afterPropertiesSet() {
        // 初始化默认数据源
        if (this.defaultDS.getUrl() != null) {
            final DataSource defaultDataSource = DataSourceBuilder.create()
                    .driverClassName(this.defaultDS.getDriverClassName())
                    .url(this.defaultDS.getUrl())
                    .username(this.defaultDS.getUsername())
                    .password(this.defaultDS.getPassword())
                    .build();
            final MutablePropertyValues properties = new MutablePropertyValues();
            properties.add("test-while-idle", true);
            properties.add("max-wait-millis", 30000);
            properties.add("validation-query", "SELECT 1");
            properties.add("time-between-eviction-runs-millis", 20000);
            properties.add("min-evictable-idle-time-millis", 28700);
            new RelaxedDataBinder(defaultDataSource).bind(properties);
            super.setDefaultTargetDataSource(defaultDataSource);
            this.defaultDataSource = defaultDataSource;
        }
        final Map<Object, Object> targetDataSources = new HashMap<>();
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet();
    }

    private void printDataSources(final Map<Object, DataSource> resolvedDataSources) {
        final Optional<Object> map = resolvedDataSources.keySet().stream().reduce((rs, key) -> rs = rs + "," + key);
        if (map.isPresent()) {
            TenantDynamicRoutingDataSource.log.info("DATASOURCES 重新加载数据源：{}", map.get());
        } else {
            TenantDynamicRoutingDataSource.log.info("DATASOURCES 重新加载数据源：{}", "");
        }
    }

    private boolean isDefaultDS(final DataSource ds) {
        return ds == this.defaultDataSource;
    }

    public void setDataSourceClient(final TenantDataSourceConfigClient dataSourceClient) {
        this.dataSourceClient = dataSourceClient;
    }
}
