package com.leadingsoft.bizfuse.base.filestorage.model;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import org.springframework.data.annotation.CreatedDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 对象数据存储记录类
 */
@Document
@Getter
@Setter
public class StorageRecord{

    public enum ObjectType {
        file, audio, picture, video
    }

    /**
     * 存储对象的编号
     */
    @Id
    private String id;

    @NotNull
    protected ObjectType objectType;

    /**
     * 文件路径，包含访问文件的全部信息
     */
    @NotBlank
    @Length(max = 1024)
    private String filePath;

    /**
     * 文件原始名称
     */
    @NotBlank
    @Length(max = 255)
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize = 0l;

    /**
     * 播放时长, 单位秒
     */
    private Integer duration = 0;

    /**
     * 存储的服务器
     */
    private String storageServer;

    /**
     * 创建日期
     *
     */
    @CreatedDate
    private Date createdDate;


}
