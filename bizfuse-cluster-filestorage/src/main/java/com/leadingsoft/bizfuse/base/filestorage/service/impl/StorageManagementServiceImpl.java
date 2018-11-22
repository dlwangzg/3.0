package com.leadingsoft.bizfuse.base.filestorage.service.impl;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.leadingsoft.bizfuse.base.filestorage.dto.DownloadUrlDTO;
import com.leadingsoft.bizfuse.base.filestorage.enums.NormalizationType;
import com.leadingsoft.bizfuse.base.filestorage.model.StorageRecord;
import com.leadingsoft.bizfuse.base.filestorage.model.StorageServerInstance;
import com.leadingsoft.bizfuse.base.filestorage.repository.StorageServerInstanceRepository;
import com.leadingsoft.bizfuse.base.filestorage.service.StorageManagementService;
import com.leadingsoft.bizfuse.base.filestorage.service.StorageRecordService;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class StorageManagementServiceImpl
        implements StorageManagementService, ApplicationListener<ContextRefreshedEvent> {
    //活跃服务器节点
    private final ArrayBlockingQueue<StorageServerInstance> storageActiveNodes = new ArrayBlockingQueue<>(100);

    //所有服务节点缓存 每隔10min获取一次
    Cache<String, StorageServerInstance> serverInstanceCache =
            CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();

    ScheduledExecutorService se = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private RestTemplate template;

    @Autowired
    private StorageServerInstanceRepository storageServerInstanceRepository;

    @Autowired
    private StorageRecordService storageRecordService;

    @Value("${local.storage.externalPort}")
    String externalPort;

    @Value("${local.storage.externalIP}")
    String externalIP;
    
    @Value("${server.ssl.enabled:false}")
    boolean sslEnabled;

    private StorageServerInstance self;

    @Override
    public String getUploadUrl(final boolean internalUrl) {
        StorageServerInstance server;
        if (this.storageActiveNodes.size() == 0) {
            throw new CustomRuntimeException("406", "无服务可用！");
        }
        if (this.storageActiveNodes.size() == 1) {
            server = this.storageActiveNodes.peek();
        } else {
            server = this.storageActiveNodes.poll();
            this.storageActiveNodes.add(server);
        }
        final String ip = internalUrl ? server.getInternalIP() : server.getExternalIP();
        final String port = internalUrl ? server.getInternalPort() : server.getExternalPort();
        String url = null;
        if (ip.startsWith("http")) {
        	url = String.format("%s:%s/upload", ip, port);
        } else {
        	String urlSchema = getUrlSchema();
            url = String.format("%s://%s:%s/upload", urlSchema, ip, port);
        }
        return url;
    }

    @Override
    public DownloadUrlDTO getDownloadUrl(final String id, final boolean internalUrl) {
        final StorageRecord model = this.storageRecordService.getStorageRecord(id);
        if (model == null) {
            throw new CustomRuntimeException("406", "文件不存在");
        }
        StorageServerInstance server = null;
        try {
            server = this.serverInstanceCache.get(model.getStorageServer(), () -> {
                return this.storageServerInstanceRepository.findOne(model.getStorageServer());
            });
        } catch (final ExecutionException e) {
            StorageManagementServiceImpl.log.warn(e.getMessage(), e);
        }
        if (server == null) {
            throw new CustomRuntimeException("406", "服务暂时不可用");
        }

        final DownloadUrlDTO dto = new DownloadUrlDTO();
        dto.setDuration(model.getDuration());
        dto.setFileName(model.getFileName());
        dto.setFileSize(model.getFileSize());
        dto.setObjectType(model.getObjectType());
        dto.setId(model.getId());
        dto.setDate(model.getCreatedDate());

        final String ip = internalUrl ? server.getInternalIP() : server.getExternalIP();
        final String port = internalUrl ? server.getInternalPort() : server.getExternalPort();
        String url = null;
        if (ip.startsWith("http")) {
        	url = String.format("%s:%s/download/%s", ip, port, model.getId());
        } else {
        	String urlSchema = getUrlSchema();
            url = String.format("%s://%s:%s/download/%s", urlSchema, ip, port, model.getId());
        }
        
        dto.setOriginalFileUrl(url + "?type=" + NormalizationType.original);
        dto.setStandardFileUrl(url + "?type=" + NormalizationType.standard);
        dto.setThumbnailFileUrl(url + "?type=" + NormalizationType.thumbnail);
        return dto;
    }

    @Override
    public String getRedirectUrlIfNeed(final StorageRecord record) {
        if (record.getStorageServer().equals(this.self.getId())) {
            return null;
        } else {
            final StorageServerInstance serverInstance =
                    this.serverInstanceCache.getIfPresent(record.getStorageServer());
            String ip = serverInstance.getExternalIP();
            String port = serverInstance.getExternalPort();
            String url = null;
            if (ip.startsWith("http")) {
            	url = String.format("%s:%s/download/%s", ip, port, record.getId());
            } else {
            	String urlSchema = getUrlSchema();
                url = String.format("%s://%s:%s/download/%s", urlSchema, ip, port, record.getId());
            }
			return url;
        }
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        final String internalIp = this.storageRecordService.getInternalIP();
        final String port = this.storageRecordService.getPort();
        if (internalIp == null) {
            throw new CustomRuntimeException("406", "服务器内网IP解析错误");
        }
        //自注册
        this.self = this.storageServerInstanceRepository.findOneByInternalIPAndInternalPort(internalIp, port);
        if (this.self == null) {
            final StorageServerInstance instance = new StorageServerInstance();
            instance.setInternalIP(internalIp);
            instance.setInternalPort(port);
            instance.setExternalIP(this.externalIP);
            instance.setExternalPort(this.externalPort);
            this.self = this.storageServerInstanceRepository.save(instance);
        }else {
            this.self.setExternalIP(this.externalIP);
            this.self.setExternalPort(this.externalPort);
            this.self = this.storageServerInstanceRepository.save(this.self);
        }

        this.se.scheduleWithFixedDelay(() -> {
            try {
                if (this.serverInstanceCache.size() == 0) {
                    final List<StorageServerInstance> serverInstances = this.storageServerInstanceRepository.findAll();
                    serverInstances.stream().forEach(x -> {
                        this.serverInstanceCache.put(x.getId(), x);
                    });
                    this.storageActiveNodes.stream().forEach(x -> {
                        if (!serverInstances.contains(x)) {
                            this.storageActiveNodes.remove(x);
                        }
                    });
                }
                for (final String id : this.serverInstanceCache.asMap().keySet()) {
                    final StorageServerInstance storageNode = this.serverInstanceCache.getIfPresent(id);
                    if (storageNode == null) {
                        return;
                    }
                    final boolean isActive = this.pingServerStatus(storageNode);
                    if (!isActive) {
                        this.storageActiveNodes.remove(storageNode);
                    } else if (!this.storageActiveNodes.contains(storageNode)) {
                        this.storageActiveNodes.add(storageNode);
                    }
                }
            } catch (final Exception e) {
                StorageManagementServiceImpl.log.error(e.getMessage(), e);
            }
        }, 0, 20, TimeUnit.SECONDS);
    }

    public boolean pingServerStatus(final StorageServerInstance storageNode) {
        final String url = /*getUrlSchema() + */ "http://" + storageNode.getInternalIP() + ":" + storageNode.getInternalPort() + "/f/info";
        try {
            return this.template.getForObject(url, boolean.class);
        } catch (final Exception e) {
            StorageManagementServiceImpl.log.warn("服务[  {}:{}  ]不可用", storageNode.getInternalIP(),
                    storageNode.getInternalPort());
            return false;
        }

    }

	private String getUrlSchema() {
		String urlSchema = "http";
        if (this.sslEnabled) {
        	urlSchema = "https";
        }
		return urlSchema;
	}
}
