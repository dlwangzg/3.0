package com.leadingsoft.bizfuse.cloud.saas.client.loadbalance;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.leadingsoft.bizfuse.cloud.saas.client.SaaSProperties;
import com.leadingsoft.bizfuse.cloud.saas.dto.ServerInstanceDTO;
import com.leadingsoft.bizfuse.cloud.saas.tenant.SecurityTenantUtils;
import com.leadingsoft.bizfuse.cloud.saas.tenant.TenantContext;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledNIWSServerList;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import lombok.extern.slf4j.Slf4j;

/**
 * 动态的租户-服务器列表负载均衡器
 *
 * @author liuyg
 */
@Slf4j
public class DynamicTenantServerListLoadBalancer extends DynamicServerListLoadBalancer<DiscoveryEnabledServer> {

    private String serviceId;

    @Autowired
    private TenantServerConfigClient tenantServerConfigClient;

    @Autowired
    private SaaSProperties SaaSProperties;

    @Override
    public List<Server> getReachableServers() {
        return this.convert(this.getTenantServers(this.upServerList));
    }

    @Override
    public List<Server> getAllServers() {
        return this.convert(this.getTenantServers(this.allServerList));
    }

    @Override
    public void initWithNiwsConfig(final IClientConfig clientConfig) {
        final String value = clientConfig.get(CommonClientConfigKey.NIWSServerListClassName);
        if (StringUtils.isBlank(value) || !value.equals(DiscoveryEnabledNIWSServerList.class.getName())) {
            clientConfig.set(CommonClientConfigKey.NIWSServerListClassName,
                    DiscoveryEnabledNIWSServerList.class.getName());
        }
        super.initWithNiwsConfig(clientConfig);
    }

    public void setServiceId(final String serviceId) {
        if (this.serviceId == null) {
            this.serviceId = serviceId;
        }
    }

    public boolean isInitiated() {
        return this.serviceId != null;
    }

    private List<Server> getTenantServers(final List<Server> servers) {
        final TenantContext tenantContext = SecurityTenantUtils.getTenantContext();

        if (tenantContext == null) {
            if (DynamicTenantServerListLoadBalancer.log.isDebugEnabled()) {
                DynamicTenantServerListLoadBalancer.log.debug("LoadBalance 线程ID：{}", Thread.currentThread().getId());
                DynamicTenantServerListLoadBalancer.log.debug("[{}] 服务无租户负载均衡策略", this.serviceId);
            }
            return servers;
        }
        final String tenantNo = tenantContext.getTenantNo();
        final List<ServerInstanceDTO> tenantServers =
                this.tenantServerConfigClient.getServers(tenantNo, this.serviceId);
        if (CollectionUtils.isEmpty(tenantServers)) {
            if (DynamicTenantServerListLoadBalancer.log.isDebugEnabled()) {
                DynamicTenantServerListLoadBalancer.log.debug("[{}] 服务无租户负载均衡配置", this.serviceId);
            }
            return servers;
        }
        final List<Server> enableServers = servers.stream().filter(server -> {
            return tenantServers.stream().anyMatch(ts -> {
                return ts.getPort().equals(server.getPort())
                        && (ts.getInternalIP().equals(server.getHost()) || ts.getPublicIP().equals(server.getHost()));
            });
        }).collect(Collectors.toList());

        // 日志
        if (DynamicTenantServerListLoadBalancer.log.isDebugEnabled()) {
            this.log(tenantNo, tenantServers, enableServers);
        }

        return enableServers;
    }

    private List<Server> convert(final List<Server> servers) {
        if (this.SaaSProperties.getIpMapping().isEmpty()) {
            return servers;
        }
        if (servers.isEmpty()) {
            return servers;
        }
        return servers.stream().map(server -> {
            final String host = server.getHost();
            if (this.SaaSProperties.getIpMapping().containsKey(host)) {
                final String targetHost = this.SaaSProperties.getIpMapping().get(host);
                final Server targetServer = new Server(targetHost, server.getPort());
                targetServer.setReadyToServe(server.isReadyToServe());
                targetServer.setAlive(server.isAlive());
                return targetServer;
            }
            return server;
        }).collect(Collectors.toList());
    }

    private void log(final String tenantNo, final List<ServerInstanceDTO> tenantServers,
            final List<Server> enableServers) {
        final StringBuilder serverList = new StringBuilder();
        tenantServers.stream().forEach(s -> {
            serverList.append(s.getInternalIP()).append(",");
        });
        DynamicTenantServerListLoadBalancer.log.debug("LOADBALANCE 租户服务器列表：{}", serverList.toString());
        final StringBuilder serverList2 = new StringBuilder();
        enableServers.stream().forEach(s -> {
            serverList2.append(s.getHost()).append(",");
        });
        DynamicTenantServerListLoadBalancer.log.debug("LOADBALANCE 返回有效租户 {} 服务器 {} 列表 {}", tenantNo,
                this.serviceId,
                serverList2.toString());
    }
}
