package com.leadingsoft.bizfuse.cloud.saas.server.service;

import java.util.List;

import com.leadingsoft.bizfuse.cloud.saas.dto.SyncBean;
import com.leadingsoft.bizfuse.cloud.saas.dto.TenantServerRelationBean;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantServerConfig;

/**
 * TenantServerConfigService
 */
public interface TenantServerConfigService {

    /**
     * 根据ID获取资源
     *
     * @param id 资源实例ID
     * @return Id所指向的资源实例
     * @throws 当Id所指向的资源不存在时，抛CustomRuntimeException异常
     */
    TenantServerConfig get(Long id);

    /**
     * 创建
     *
     * @param model 资源实例
     * @return 创建后的对象
     */
    TenantServerConfig create(TenantServerConfig model);

    /**
     * 更新
     *
     * @param model 编辑后的资源实例
     * @return 修改后的对象
     */
    TenantServerConfig update(TenantServerConfig model);

    /**
     * 删除
     *
     * @param id 资源实例ID
     */
    void delete(Long id);

    /**
     * 存在新的变更，则获取所有记录，否则返回空列表
     *
     * @param lastModifiedTime 上次变更时间
     * @return
     */
    SyncBean<List<TenantServerRelationBean>> getAllIfExistModification(long lastModifiedTime);
}
