package com.leadingsoft.bizfuse.cloud.saas.server.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.cloud.saas.dto.TenantServerRelationBean;
import com.leadingsoft.bizfuse.cloud.saas.server.model.QTenantServerConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantServerConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.TenantServerConfigRepositoryCustom;
import com.leadingsoft.bizfuse.common.jpa.repository.AbstractRepository;
import com.leadingsoft.bizfuse.common.web.support.Searchable;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;

@Component
public class TenantServerConfigRepositoryImpl extends AbstractRepository implements TenantServerConfigRepositoryCustom {

    @Override
    public Page<TenantServerConfig> page(final Pageable pageable, final Searchable searchable) {

        final BooleanBuilder where = this.buildWhere(searchable);
        return super.search(where, pageable, QTenantServerConfig.tenantServerConfig);
    }

    @Override
    public List<TenantServerConfig> list(final Searchable searchable) {
        final QTenantServerConfig qTSC = QTenantServerConfig.tenantServerConfig;
        final BooleanBuilder where = this.buildWhere(searchable);
        return super.query().selectFrom(qTSC).where(where).fetch();
    }

    @Override
    public List<TenantServerRelationBean> findAll() {
        final QTenantServerConfig qTSC = QTenantServerConfig.tenantServerConfig;
        return this.query()
                .select(Projections.bean(TenantServerRelationBean.class, qTSC.tenantNo,
                        qTSC.serverInstance.id.as("serverInstanceId")))
                .from(qTSC)
                .fetch();
    }

    private BooleanBuilder buildWhere(final Searchable searchable) {
        final QTenantServerConfig qTSC = QTenantServerConfig.tenantServerConfig;
        final BooleanBuilder where = new BooleanBuilder();

        // 租户编码
        where.and(super.equalsStr(qTSC.tenantNo, searchable, "tenantNo"));
        // 服务器类型
        where.and(super.equalsStr(qTSC.serverInstance.type, searchable, "serverType"));
        // 服务器内网IP
        where.and(super.containsStr(qTSC.serverInstance.internalIP, searchable, "internalIP"));
        // 服务器外网IP
        where.and(super.containsStr(qTSC.serverInstance.publicIP, searchable, "publicIP"));
        return where;
    }

    @Override
    protected Class<?> getModelClass() {
        return TenantServerConfig.class;
    }

}
