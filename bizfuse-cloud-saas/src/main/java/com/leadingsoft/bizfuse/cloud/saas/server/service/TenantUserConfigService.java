package com.leadingsoft.bizfuse.cloud.saas.server.service;

import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantUserConfig;

/**
 * TenantUserConfigService
 */
public interface TenantUserConfigService {

    /**
     * 根据ID获取资源
     *
     * @param id 资源实例ID
     * @return Id所指向的资源实例
     * @throws 当Id所指向的资源不存在时，抛CustomRuntimeException异常
     */
    TenantUserConfig get(Long id);

    /**
     * 创建
     *
     * @param model 资源实例
     * @return 创建后的对象
     */
    TenantUserConfig create(TenantUserConfig model);

    /**
     * 更新
     *
     * @param model 编辑后的资源实例
     * @return 修改后的对象
     */
    TenantUserConfig update(TenantUserConfig model);
    
    /**
     * 删除
     *
     * @param id 资源实例ID
     */
    void delete(Long id);

}
