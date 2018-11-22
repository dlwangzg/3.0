package com.leadingsoft.bizfuse.cloud.saas.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncBean<T> {

    private long lastModifiedTime;

    private T data;
}
