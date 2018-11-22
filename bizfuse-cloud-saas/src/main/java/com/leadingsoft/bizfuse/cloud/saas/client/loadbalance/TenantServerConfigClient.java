package com.leadingsoft.bizfuse.cloud.saas.client.loadbalance;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.cloud.saas.client.ConfigServerRestService;
import com.leadingsoft.bizfuse.cloud.saas.client.SaaSProperties;
import com.leadingsoft.bizfuse.cloud.saas.client.datasource.TenantDataSourceConfigClient;
import com.leadingsoft.bizfuse.cloud.saas.dto.ServerInstanceDTO;
import com.leadingsoft.bizfuse.cloud.saas.dto.TenantServerRelationBean;
import com.leadingsoft.bizfuse.common.web.dto.result.ListResultDTO;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 租户服务器配置客户端，定时获取最新的租户服务器配置
 *
 * @author liuyg
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "bizfuse.saas.loadbalance.enabled", matchIfMissing = true, havingValue = "true")
public class TenantServerConfigClient {

    private final ParameterizedTypeReference<ListResultDTO<TenantServerRelationBean>> TSRTYPT =
            new ParameterizedTypeReference<ListResultDTO<TenantServerRelationBean>>() {
            };
    private final ParameterizedTypeReference<ListResultDTO<ServerInstanceDTO>> SITYPT =
            new ParameterizedTypeReference<ListResultDTO<ServerInstanceDTO>>() {
            };

    @Autowired
    private SaaSProperties saaSProperties;
    @Autowired
    private ConfigServerRestService configServerRestService;
    @Autowired(required = false)
    private TenantDataSourceConfigClient tenantDataSourceConfigClient;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private Map<String, List<ServerInstanceDTO>> localTenantTypeServers = new HashMap<>();
    private long tenantServerRelationLastModifiedTime = 0L;
    private List<TenantServerRelationBean> localTenantServersCache = Collections.emptyList();
    private long serverLastModifiedTime = 0L;
    private List<ServerInstanceDTO> localServersCache = Collections.emptyList();
    private ServerInstanceDTO nativeServer;

    @PostConstruct
    public void start() {
        this.executor.scheduleWithFixedDelay(() -> {
            if (TenantServerConfigClient.log.isDebugEnabled()) {
                TenantServerConfigClient.log.debug("开始同步租户服务器配置");
            }
            try {
                this.syncConfigs();
            } catch (final Exception e) {
                TenantServerConfigClient.log.warn("同步租户服务器配置失败", e);
            }
            if (TenantServerConfigClient.log.isDebugEnabled()) {
                TenantServerConfigClient.log.debug("同步租户服务器配置完成");
            }
        }, 5, 60, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stop() {
        this.executor.shutdown();
    }

    /**
     * 获取租户指定类型的服务器实例列表
     *
     * @param tenantNo
     * @param serverType
     * @return
     */
    public List<ServerInstanceDTO> getServers(final String tenantNo, final String serverType) {
        final List<ServerInstanceDTO> servers = this.localTenantTypeServers.get(tenantNo + serverType);
        if (((servers == null) || (servers.size() == 0)) && TenantServerConfigClient.log.isDebugEnabled()) {
            TenantServerConfigClient.log.debug("获取不到租户指定类型的服务器实例：{}:{}", tenantNo, serverType);
            TenantServerConfigClient.log.debug("本地缓存：{}", JsonUtils.pojoToJson(this.localTenantTypeServers));
        }
        return servers;
    }

    /**
     * 获取特定服务器实例分配的租户列表
     *
     * @param serverId
     * @return
     */
    public List<String> getServerTenants() {
        try {
            final ServerInstanceDTO nativeServer = this.getNativeServer();
            if (nativeServer == null) {
                TenantServerConfigClient.log.warn("获取本机服务器实例信息失败");
                return null;
            }
            List<TenantServerRelationBean> tenantServers = this.localTenantServersCache;
            if (this.isEmpty(tenantServers)) {
                tenantServers = this.syncTenantServerRelations();
            }
            if (this.isEmpty(tenantServers)) {
                TenantServerConfigClient.log.warn("没有租户、服务器配置信息");
                return null;
            }
            return tenantServers.stream().filter(config -> config.getServerInstanceId().equals(nativeServer.getId()))
                    .map(config -> config.getTenantNo()).collect(Collectors.toList());
        } catch (final Exception e) {
            TenantServerConfigClient.log.error("获取服务器所属的租户列表失败", e);
            return null;
        }
    }

    /**
     * 获取本机服务器信息
     *
     * @return
     * @throws SocketException
     */
    private ServerInstanceDTO getNativeServer() throws SocketException {
        if (this.nativeServer != null) {
            return this.nativeServer;
        }
        List<ServerInstanceDTO> servers = this.localServersCache;
        if (servers == null) {
            servers = this.syncServers();
        }
        if (servers == null) {
            TenantServerConfigClient.log.warn("没有服务器实例信息");
            return null;
        }
        final Set<String> nativeIps = this.getNativeIP();

        final Optional<ServerInstanceDTO> nativeServer = servers.stream().filter(server -> {
            return (this.saaSProperties.getServicePort() == server.getPort())
                    && (nativeIps.contains(server.getInternalIP()) || nativeIps.contains(server.getPublicIP()));
        }).findFirst();
        if (nativeServer.isPresent()) {
            this.nativeServer = nativeServer.get();
        }
        return this.nativeServer;
    }

    private void syncConfigs() throws Exception {
        // 同步服务器列表
        final List<ServerInstanceDTO> servers = this.syncServers();
        // 同步租户和服务器关系
        final boolean isFirstSync = this.tenantServerRelationLastModifiedTime == 0L;
        final List<TenantServerRelationBean> tenantServers = this.syncTenantServerRelations();
        if (this.isEmpty(servers) && this.isEmpty(tenantServers)) {
            // 无变更或无数据
            return;
        }
        if (!this.isEmpty(servers)) {
            this.localServersCache = servers;
        }
        if (!this.isEmpty(tenantServers)) {
            this.updateTenantDatasources(isFirstSync, tenantServers);
            this.localTenantServersCache = tenantServers;
        }
        if (this.isEmpty(this.localServersCache) || this.isEmpty(this.localTenantServersCache)) {
            return; // 数据不全
        }

        // 构造租户-》服务器类型-》服务器列表
        TenantServerConfigClient.log.info("开始同步服务器配置");
        final Map<String, List<ServerInstanceDTO>> tenantTypeServers = new HashMap<>();
        final Map<Long, ServerInstanceDTO> serverMap =
                this.localServersCache.stream().collect(Collectors.toMap(o -> o.getId(), o -> o));
        this.localTenantServersCache.stream().forEach(value -> {
            final ServerInstanceDTO server = serverMap.get(value.getServerInstanceId());
            if (server == null) {
                return;
            }
            final String key = value.getTenantNo() + server.getType();
            List<ServerInstanceDTO> typeServers = tenantTypeServers.get(key);
            if (typeServers == null) {
                typeServers = new ArrayList<>();
                tenantTypeServers.put(key, typeServers);
            }
            typeServers.add(server);
        });
        this.localTenantTypeServers = tenantTypeServers;
        TenantServerConfigClient.log.info("服务器配置同步完成");
        TenantServerConfigClient.log.info(JsonUtils.pojoToJson(this.localServersCache));
        TenantServerConfigClient.log.info(JsonUtils.pojoToJson(this.localTenantServersCache));
        TenantServerConfigClient.log.info(JsonUtils.pojoToJson(this.localTenantTypeServers));
    }

    private void updateTenantDatasources(final boolean isFirstSync, final List<TenantServerRelationBean> tenantServers)
            throws Exception {
        if (this.tenantDataSourceConfigClient == null) {
            return;
        }
        if (!isFirstSync && (this.getNativeServer() != null)) { // 判断如存在本服务的租户变更，通知更新数据源
            if (TenantServerConfigClient.log.isDebugEnabled()) {
                TenantServerConfigClient.log.debug("租户服务器配置变更，同步服务的数据源...");
            }
            final List<String> oldTenants = this.localTenantServersCache.stream()
                    .filter(ts -> ts.getServerInstanceId().equals(this.nativeServer.getId()))
                    .map(ts -> ts.getTenantNo())
                    .collect(Collectors.toList());
            final List<String> newTenants = tenantServers.stream()
                    .filter(ts -> ts.getServerInstanceId().equals(this.nativeServer.getId()))
                    .map(ts -> ts.getTenantNo())
                    .collect(Collectors.toList());
            final List<String> addTenants =
                    newTenants.stream().filter(t -> !oldTenants.contains(t)).collect(Collectors.toList());
            final List<String> removedTenants =
                    oldTenants.stream().filter(t -> !newTenants.contains(t)).collect(Collectors.toList());
            this.tenantDataSourceConfigClient.addTenantsDatasources(addTenants);
            this.tenantDataSourceConfigClient.removeTenantsDatasources(removedTenants);
        }
    }

    private List<TenantServerRelationBean> syncTenantServerRelations() {
        try {
            final String url = "/saas/tenantServerConfigs/sync/" + this.tenantServerRelationLastModifiedTime;
            final ResponseEntity<ListResultDTO<TenantServerRelationBean>> rs =
                    this.configServerRestService.get(url, this.TSRTYPT);
            if (rs.getBody().isFailure()) {
                TenantServerConfigClient.log.warn("获取租户服务器关系失败：{}", JsonUtils.pojoToJson(rs.getBody().getErrors()));
                return null;
            } else {
                if (rs.getBody().getTimestamp() != null) {
                    this.tenantServerRelationLastModifiedTime = rs.getBody().getTimestamp().getTime();
                }
                return rs.getBody().getData();
            }
        } catch (final Exception e) {
            TenantServerConfigClient.log.warn(e.getMessage());
            return null;
        }
    }

    private List<ServerInstanceDTO> syncServers() {
        try {
            final String url = "/saas/serverInstances/sync/" + this.serverLastModifiedTime;
            final ResponseEntity<ListResultDTO<ServerInstanceDTO>> rs =
                    this.configServerRestService.get(url, this.SITYPT);
            if (rs.getBody().isFailure()) {
                TenantServerConfigClient.log.warn("获取服务器列表失败：{}", JsonUtils.pojoToJson(rs.getBody().getErrors()));
                return null;
            } else {
                if (rs.getBody().getTimestamp() != null) {
                    this.serverLastModifiedTime = rs.getBody().getTimestamp().getTime();
                    return rs.getBody().getData();
                } else {
                    return null;
                }
            }
        } catch (final Exception e) {
            TenantServerConfigClient.log.warn(e.getMessage());
            return null;
        }
    }

    private Set<String> getNativeIP() throws SocketException {
        final Set<String> allIps = new HashSet<>();
        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            final Enumeration<InetAddress> inetAddresses = interfaces.nextElement().getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                final InetAddress ip = inetAddresses.nextElement();
                allIps.add(ip.getHostAddress());
            }
        }
        return allIps;
    }

    private boolean isEmpty(final List<?> servers) {
        return (servers == null) || servers.isEmpty();
    }
}
