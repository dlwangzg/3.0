package com.leadingsoft.bizfuse.base.filestorage.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leadingsoft.bizfuse.base.filestorage.model.StorageRecord.ObjectType;

/**
 * 对象数据存储记录类
 */
public class StorageRecordDTO {

    private static final long serialVersionUID = -1538901115520003737L;

    /**
     * 存储对象的编号（不用long ID是为了文件ID不容易推测出来）
     */
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
     * 创建日期
     */
    private Date createdDate;

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(final Long fileSize) {
        this.fileSize = fileSize;
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public void setObjectType(final ObjectType storeType) {
        this.objectType = storeType;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public boolean isNew() {
        if (this.getId() == null) {
            return true;
        }
        return false;
    }
}
