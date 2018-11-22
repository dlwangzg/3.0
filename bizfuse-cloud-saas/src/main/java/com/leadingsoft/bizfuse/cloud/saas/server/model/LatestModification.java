package com.leadingsoft.bizfuse.cloud.saas.server.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.leadingsoft.bizfuse.common.jpa.model.AbstractModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class LatestModification extends AbstractModel {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(unique = true)
    private String model;

    private long lastModifiedTime;

    @Version
    private long version;

    public void markModified() {
        this.lastModifiedTime = System.currentTimeMillis();
    }
}
