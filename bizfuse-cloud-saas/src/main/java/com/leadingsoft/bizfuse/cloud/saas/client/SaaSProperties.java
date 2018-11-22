package com.leadingsoft.bizfuse.cloud.saas.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class SaaSProperties {

    /**
     * 配置服务器的服务ID
     */
    @Value("${bizfuse.saas.configServerId}")
    private String configServerId;
    /**
     * 当前服务的ID
     */
    @Value("${spring.application.name}")
    private String localServiceId;
    /**
     * 当前REST服务的端口
     */
    @Value("${server.port}")
    private int servicePort;

    @Value("${bizfuse.saas.iptables:NULL}")
    private String iptables;

    @Value("${bizfuse.saas.tenantCheck.whitePatterns:NULL}")
    private String tenantCheckWhitePatternsStr;

    private final Map<String, String> ipMapping = new HashMap<>();

    private List<String> tenantCheckWhitePatterns;

    public List<String> getTenantCheckWhitePatterns() {
        if (this.tenantCheckWhitePatterns == null) {
            this.tenantCheckWhitePatterns = new ArrayList<>();
            if (this.tenantCheckWhitePatternsStr.equals("NULL")) {
                return this.tenantCheckWhitePatterns;
            }
            final String[] patterns = this.tenantCheckWhitePatternsStr.split(",");
            for (final String pattern : patterns) {
                final String p = pattern.trim();
                if (StringUtils.isBlank(p)) {
                    continue;
                }
                this.tenantCheckWhitePatterns.add(p);
            }
        }
        return this.tenantCheckWhitePatterns;
    }

    public Map<String, String> getIpMapping() {
        if (!this.ipMapping.isEmpty()) {
            return this.ipMapping;
        }
        if (this.iptables.equals("NULL")) {
            return this.ipMapping;
        }
        final String[] configs = this.iptables.split(",");
        for (final String config : configs) {
            final String[] map = config.split(":");
            this.ipMapping.put(map[0], map[1]);
        }
        return this.ipMapping;
    }
}
