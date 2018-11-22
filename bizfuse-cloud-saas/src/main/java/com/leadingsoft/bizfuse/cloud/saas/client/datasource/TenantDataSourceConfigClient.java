package com.leadingsoft.bizfuse.cloud.saas.client.datasource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.cloud.saas.client.ConfigServerRestService;
import com.leadingsoft.bizfuse.cloud.saas.client.SaaSProperties;
import com.leadingsoft.bizfuse.cloud.saas.client.loadbalance.TenantServerConfigClient;
import com.leadingsoft.bizfuse.cloud.saas.dto.TenantDataSourceConfigDTO;
import com.leadingsoft.bizfuse.cloud.saas.dto.TenantDataSourceSyncBean;
import com.leadingsoft.bizfuse.common.web.dto.result.ListResultDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ConditionalOnProperty(name = "bizfuse.saas.dynamicDataSource.enabled", matchIfMissing = true, havingValue = "true")
public class TenantDataSourceConfigClient {

    private final ParameterizedTypeReference<ListResultDTO<TenantDataSourceConfigDTO>> RSTYPT =
            new ParameterizedTypeReference<ListResultDTO<TenantDataSourceConfigDTO>>() {
            };

    @Autowired
    private SaaSProperties saaSProperties;
    @Autowired
    private ConfigServerRestService configServerRestService;
    @Autowired
    private TenantServerConfigClient tenantServerConfigClient;
    @Autowired
    private TenantDynamicRoutingDataSource tenantDynamicRoutingDataSource;

    private long lastModifiedTime = 0l;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void start() {
        this.tenantDynamicRoutingDataSource.setDataSourceClient(this);
        this.executor.scheduleWithFixedDelay(() -> {
            if (TenantDataSourceConfigClient.log.isDebugEnabled()) {
                TenantDataSourceConfigClient.log.debug("开始同步服务器数据源");
            }
            try {
                this.syncConfigs();
            } catch (final Exception e) {
                TenantDataSourceConfigClient.log.warn("同步服务器数据源失败", e);
            }
            if (TenantDataSourceConfigClient.log.isDebugEnabled()) {
                TenantDataSourceConfigClient.log.debug("同步服务器数据源完成");
            }
        }, 10, 60, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stop() {
        this.executor.shutdown();
    }

    /**
     * 添加租户的数据源
     *
     * @param tenantNos
     * @throws Exception
     */
    public void addTenantsDatasources(final List<String> tenantNos) throws Exception {
        if (tenantNos.isEmpty()) {
            return;
        }
        if (TenantDataSourceConfigClient.log.isDebugEnabled()) {
            TenantDataSourceConfigClient.log.debug("服务器新分配租户：[{}]，准备同步数据源...",
                    tenantNos.stream().reduce((rs, e) -> rs = rs + "," + e).get());
        }
        final List<TenantDataSourceConfigDTO> dataSources = this.syncNativeServerDataSources(tenantNos, 0L);
        if ((dataSources == null) || dataSources.isEmpty()) {
            return;
        }
        if (TenantDataSourceConfigClient.log.isDebugEnabled()) {
            TenantDataSourceConfigClient.log.debug("服务器新增数据源：[{}]",
                    dataSources.stream().map(ds -> ds.getTenantNo() + ds.getUrl()).reduce((rs, e) -> rs = rs + "," + e)
                            .get());
        }
        final Map<String, DataSource> allDataSources = this.convertDataSources(dataSources);
        this.tenantDynamicRoutingDataSource.updateDataSources(allDataSources);
    }

    /**
     * 移除租户的数据源
     *
     * @param tenantNos
     * @throws Exception
     */
    public void removeTenantsDatasources(final List<String> tenantNos) throws Exception {
        if (tenantNos.isEmpty()) {
            return;
        }
        if (TenantDataSourceConfigClient.log.isDebugEnabled()) {
            TenantDataSourceConfigClient.log.debug("服务器移除租户：[{}]，移除租户数据源",
                    tenantNos.stream().reduce((rs, e) -> rs = rs + "," + e).get());
        }
        this.tenantDynamicRoutingDataSource.removeDataSource(tenantNos);
    }

    public DataSource syncTenantDataSource(final String tenant) {
        final List<TenantDataSourceConfigDTO> dsConfigs = this.syncNativeServerDataSources(Arrays.asList(tenant), 0L);
        if ((dsConfigs == null) || dsConfigs.isEmpty()) {
            return null;
        }
        return this.convertDataSources(dsConfigs).get(tenant);
    }

    /**
     * 增量同步租户数据源
     *
     * @throws Exception
     */
    private void syncConfigs() throws Exception {
        final List<String> serverSelftTenantNos = this.tenantServerConfigClient.getServerTenants();
        List<String> serverPublicTenantNos = this.tenantDynamicRoutingDataSource.getUnhitDataSourceTenants();
        List<String> allTenants = new ArrayList<>();
        boolean syncServerSelfTenants = (serverSelftTenantNos != null && !serverSelftTenantNos.isEmpty());
        boolean syncServerPublicTenants = (serverPublicTenantNos != null && !serverPublicTenantNos.isEmpty());
        if (syncServerSelfTenants) {
            allTenants.addAll(serverSelftTenantNos);
        }
        if (syncServerPublicTenants) {
        	allTenants.addAll(serverPublicTenantNos);
        }
        final List<TenantDataSourceConfigDTO> dataSources =
                this.syncNativeServerDataSources(allTenants, this.lastModifiedTime);
        if ((dataSources == null) || dataSources.isEmpty()) {
            return;
        }
        if (TenantDataSourceConfigClient.log.isDebugEnabled()) {
            TenantDataSourceConfigClient.log.debug("服务器租户数据源变更：[{}]",
                    dataSources.stream().map(ds -> ds.getTenantNo() + ds.getUrl()).reduce((rs, e) -> rs = rs + "," + e)
                            .get());
        }
        if (syncServerSelfTenants) {
        	List<TenantDataSourceConfigDTO> serverSelftDS = dataSources.stream()
            		.filter(ds -> serverSelftTenantNos.contains(ds.getTenantNo()))
            		.collect(Collectors.toList());
        	if (!serverSelftDS.isEmpty()) {
        		final Map<String, DataSource> selfDataSources = this.convertDataSources(serverSelftDS);
        		if (this.lastModifiedTime == 0L) { // 初次加载数据源
                    this.tenantDynamicRoutingDataSource.reloadDataSources(selfDataSources);
                } else {
                    this.tenantDynamicRoutingDataSource.updateDataSources(selfDataSources);
                }
        	}
        }
        if (syncServerPublicTenants) {
        	List<TenantDataSourceConfigDTO> serverPublicDS = dataSources.stream()
            		.filter(ds -> serverPublicTenantNos.contains(ds.getTenantNo()))
            		.collect(Collectors.toList());
        	if (!serverPublicDS.isEmpty()) {
        		final Map<String, DataSource> publicDataSources = this.convertDataSources(serverPublicDS);
        		tenantDynamicRoutingDataSource.updateUnhitDataSources(publicDataSources);
        	}
        }
    }

    private Map<String, DataSource> convertDataSources(final List<TenantDataSourceConfigDTO> dataSources) {
        final Map<String, DataSource> allDataSources = new HashMap<>();
        dataSources.forEach(ds -> {
            final DataSource dataSource =
                    DataSourceBuilder.create().driverClassName(ds.getDriverClassName())
                            .url(ds.getUrl()).username(ds.getUsername()).password(ds.getPassword()).build();

            final MutablePropertyValues properties = new MutablePropertyValues();
            properties.add("test-while-idle", true);
            properties.add("max-wait-millis", 30000);
            properties.add("validation-query", "SELECT 1");
            properties.add("time-between-eviction-runs-millis", 20000);
            properties.add("min-evictable-idle-time-millis", 28700);
            new RelaxedDataBinder(dataSource).bind(properties);
            allDataSources.put(ds.getTenantNo(), dataSource);
        });
        return allDataSources;
    }

    private List<TenantDataSourceConfigDTO> syncNativeServerDataSources(final List<String> tenantNos,
            final long syncTime) {
        if (tenantNos == null || tenantNos.isEmpty()) {
            return null;
        }

        final TenantDataSourceSyncBean search = new TenantDataSourceSyncBean();
        search.setLastModifiedTime(syncTime);
        search.setServerType(this.saaSProperties.getLocalServiceId());
        search.setTenantNos(tenantNos);

        final String url = "/saas/tenantDataSourceConfigs/sync";

        try {
            final ResponseEntity<ListResultDTO<TenantDataSourceConfigDTO>> rs =
                    this.configServerRestService.post(url, search, this.RSTYPT);
            if (rs.getBody().getTimestamp() != null) {
                this.lastModifiedTime = rs.getBody().getTimestamp().getTime();
                return rs.getBody().getData();
            } else {
                return null;
            }
        } catch (final Exception e) {
            TenantDataSourceConfigClient.log.warn(e.getMessage());
            return null;
        }
    }
}
