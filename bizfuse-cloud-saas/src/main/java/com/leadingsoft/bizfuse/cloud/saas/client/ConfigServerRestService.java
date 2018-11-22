package com.leadingsoft.bizfuse.cloud.saas.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

@Component
public class ConfigServerRestService {
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private SaaSProperties SaaSProperties;

    public URI getUri(final String url) {
        final List<ServiceInstance> services =
                this.discoveryClient.getInstances(this.SaaSProperties.getConfigServerId());
        final Optional<ServiceInstance> find = services.stream().findAny();
        if (!find.isPresent()) {
            throw new RestClientException("无有效的SaaS配置服务器实例");
        }
        try {
            final ServiceInstance service = find.get();
            String host = service.getHost();
            if (this.SaaSProperties.getIpMapping().containsKey(host)) {
                host = this.SaaSProperties.getIpMapping().get(host);
            }
            return new URI(String.format("http://%s:%s" + url, host, service.getPort()));
        } catch (final URISyntaxException e) {
            throw new CustomRuntimeException("500", "URI地址无效");
        }
    }

    public <T, E> ResponseEntity<T> post(
            final String url,
            final E body,
            final ParameterizedTypeReference<T> resultType) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        final RequestEntity<E> reqEntity = new RequestEntity<E>(body, headers, HttpMethod.POST, this.getUri(url));
        return new RestTemplate().exchange(reqEntity, resultType);
    }

    public <T> ResponseEntity<T> get(
            final String url,
            final ParameterizedTypeReference<T> resultType) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        final RequestEntity<Object> reqEntity =
                new RequestEntity<Object>(null, headers, HttpMethod.GET, this.getUri(url));
        return new RestTemplate().exchange(reqEntity, resultType);
    }
}
