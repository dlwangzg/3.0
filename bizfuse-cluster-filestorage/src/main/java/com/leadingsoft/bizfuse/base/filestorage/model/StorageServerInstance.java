package com.leadingsoft.bizfuse.base.filestorage.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * 文件服务器实例详情类
 */
@Document
@Getter
@Setter
public class StorageServerInstance{


    @Id
    private String id;

    /**
     * 服务器内网IP
     */
    @NotBlank
    private String internalIP;

    /**
     * 服务端口
     */
    @NotBlank
    private String internalPort;

    private String externalIP;

    private String externalPort;

    /**
     * 服务的ip和端口一致则为同一服务
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StorageServerInstance)){
            return false;
        }
        StorageServerInstance anServer=(StorageServerInstance)obj;
        if(this.getInternalIP().equals(anServer.getInternalIP())&&this.getInternalPort().equals(anServer.getInternalPort())){
            return true;
        }
        return false;
    }
}
