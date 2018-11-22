package com.leadingsoft.bizfuse.cloud.saas.client.registry;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.cloud.saas.client.ConfigServerRestService;
import com.leadingsoft.bizfuse.cloud.saas.client.SaaSProperties;
import com.leadingsoft.bizfuse.cloud.saas.dto.ServerInstanceDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 自注册服务
 *
 * @author liuyg
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "bizfuse.saas.self-registry.enabled", matchIfMissing = true, havingValue = "true")
public class SelfRegistryService implements ApplicationListener<ContextRefreshedEvent> {
	private final ParameterizedTypeReference<ResultDTO<ServerInstanceDTO>> SITYPT = new ParameterizedTypeReference<ResultDTO<ServerInstanceDTO>>() {
	};

	/**
	 * 内网（10.0.1.*）
	 */
	@Value("${bizfuse.saas.internalNetwork:}")
	private String internalNetwrok;
	@Autowired
	private SaaSProperties saaSProperties;
	@Autowired
	private ConfigServerRestService configServerRestService;
	@Autowired
	private InetUtils inetUtils;

	private boolean registryTaskStarted = false;

	public void registrySelf() {
		final String internalIP = this.getInternalIP();
		if (internalIP == null) {
			SelfRegistryService.log.warn("没有解析出内网IP，不执行自注册处理。请检查是否内网属性未指定");
			return;
		}
		final ServerInstanceDTO serverInstance = new ServerInstanceDTO();
		serverInstance.setInternalIP(internalIP);
		serverInstance.setPublicIP("");
		serverInstance.setPort(this.saaSProperties.getServicePort());
		serverInstance.setType(this.saaSProperties.getLocalServiceId());
		serverInstance.setRemarks(this.saaSProperties.getLocalServiceId());

		final ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			try {
				Thread.sleep(10000);
			} catch (final Exception e) {
			}
			int retryTimes = 30;
			while ((retryTimes > 0) && !this.registryServerInstance(serverInstance)) {
				--retryTimes;
				try {
					Thread.sleep(10000);
				} catch (final Exception e) {
					SelfRegistryService.log.warn("服务自注册发生异常", e);
				}
			}
		});
		executor.shutdown();
	}

	private boolean registryServerInstance(final ServerInstanceDTO serverInstance) {
		try {
			final String url = "/saas/serverInstances";
			final ResponseEntity<ResultDTO<ServerInstanceDTO>> rs = this.configServerRestService.post(url,
					serverInstance, this.SITYPT);
			if (rs.getBody().isFailure()) {
				SelfRegistryService.log.warn("服务自注册失败：{}", JsonUtils.pojoToJson(rs.getBody().getErrors()));
				return false;
			} else {
				SelfRegistryService.log.info("服务自注册成功");
				return true;
			}
		} catch (final Exception e) {
			SelfRegistryService.log.warn(e.getMessage());
			return false;
		}
	}

	private String getInternalIP() {
		if (StringUtils.isBlank(this.internalNetwrok)) {
			InetAddress address = inetUtils.findFirstNonLoopbackAddress();
			if (address != null) {
				return address.getHostAddress();
			}
		} else {
			Enumeration<NetworkInterface> interfaces;
			try {
				interfaces = NetworkInterface.getNetworkInterfaces();
			} catch (final SocketException e) {
				SelfRegistryService.log.error("获取网卡信息失败！", e);
				return null;
			}
			while (interfaces.hasMoreElements()) {
				final Enumeration<InetAddress> inetAddresses = interfaces.nextElement().getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					final InetAddress ip = inetAddresses.nextElement();
					final String ipStr = ip.getHostAddress();
					if (this.matches(ipStr, this.internalNetwrok)) {
						return ipStr;
					}
				}
			}
		}
		return null;
	}

	private boolean matches(final String ipAddress, final String network) {
		final String[] ipElements = ipAddress.split("\\.");
		final String[] networkElements = network.split("\\.");
		if (ipElements.length != networkElements.length) {
			return false;
		}
		for (int i = 0; i < ipElements.length; i++) {
			if (!ipElements[i].equals(networkElements[i]) && !networkElements[i].equals("*")) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		if (!this.registryTaskStarted) {
			this.registryTaskStarted = true;
			this.registrySelf();
		}
	}
}
