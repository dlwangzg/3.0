package com.leadingsoft.bizfuse.base.filestorage.service.impl;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.leadingsoft.bizfuse.base.filestorage.model.StorageRecord;
import com.leadingsoft.bizfuse.base.filestorage.model.StorageServerInstance;
import com.leadingsoft.bizfuse.base.filestorage.repository.StorageRecordRepository;
import com.leadingsoft.bizfuse.base.filestorage.repository.StorageServerInstanceRepository;
import com.leadingsoft.bizfuse.base.filestorage.service.StorageRecordService;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * StorageRecordService 实现类
 */
@Service
@Transactional
@Slf4j
public class StorageRecordServiceImpl implements StorageRecordService {

    private String serverId;
    @Autowired
    private StorageRecordRepository storageRecordRepository;

    @Autowired
    private StorageServerInstanceRepository storageServerInstanceRepository;

    @Value("${local.storage.internalIP:}")
    String internalIP;
    @Value("${spring.cloud.inetutils.ignored-interfaces:}")
    List<String> ignoredInterfaces = new ArrayList<>();

    @Value("${server.port}")
    String port;

    @Override
    public StorageRecord getStorageRecord(@NonNull final String id) {
        final StorageRecord model = this.storageRecordRepository.findOne(id);
        if (model == null) {
            throw new CustomRuntimeException("404", String.format("查找的资源[%s]不存在.", id));
        }
        return model;
    }

    @Override
    public StorageRecord createStorageRecord(final StorageRecord model) {
        model.setStorageServer(getServerId());
        return this.storageRecordRepository.save(model);
    }

    @Override
    public StorageRecord updateStorageRecord(final StorageRecord model) {
        model.setStorageServer(getServerId());
        return this.storageRecordRepository.save(model);
    }

    @Override
    public void deleteStorageRecord(@NonNull final String id) {
        this.storageRecordRepository.delete(id);
    }

    @Override
    public List<StorageRecord> getStorageRecords(final List<String> storageIds) {
        if (storageIds.isEmpty()) {
            return Collections.emptyList();
        }
        return this.storageRecordRepository.findAllByIdIn(storageIds);
    }

    private String getServerId() {
        if(this.serverId!=null){
            return this.serverId;
        }
        String ipStr=this.getInternalIP();
        if(ipStr==null){
            throw new CustomRuntimeException("406","服务器内网IP解析错误");
        }
        StorageServerInstance serverInstance=this.storageServerInstanceRepository.findOneByInternalIPAndInternalPort(ipStr,port);
        if(serverInstance==null){
            throw new CustomRuntimeException("406","服务暂时不可用");
        }
        this.serverId=serverInstance.getId();
        return this.serverId;
    }

    @Override
    public String getInternalIP(){
        if(StringUtils.hasText(this.internalIP) && !this.internalIP.contains("*"))
            return this.internalIP;
        Enumeration<NetworkInterface> interfaces;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (final SocketException e) {
            log.error("获取网卡信息失败！", e);
            return null;
        }
        while (interfaces.hasMoreElements()) {
            NetworkInterface inter = interfaces.nextElement();
            try {
				if (!inter.isUp()) {
					continue;
				}
			} catch (SocketException e) {
			}
			final Enumeration<InetAddress> inetAddresses = inter.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                final InetAddress ip = inetAddresses.nextElement();
                final String ipStr = ip.getHostAddress();
                if (this.matches(ipStr, inter.getDisplayName())) {
                    return ipStr;
                }
            }
        }
        return null;
    }

    private boolean matches(final String ipAddress, String interfaceName) {
    	if (StringUtils.hasText(this.internalIP)) {
    		final String[] ipElements = ipAddress.split("\\.");
            final String[] networkElements = internalIP.split("\\.");
            if (ipElements.length != networkElements.length) {
                return false;
            }
            for (int i = 0; i < ipElements.length; i++) {
                if (!ipElements[i].equals(networkElements[i]) && !networkElements[i].equals("*")) {
                    return false;
                }
            }
            return true;
    	} else {
    		return !this.ignoredInterfaces.contains(interfaceName);
    	}
    }

    @Override
    public String getPort(){
        return this.port;
    }
}
