package com.leadingsoft.bizfuse.cloud.saas.server.repository.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.leadingsoft.bizfuse.cloud.saas.dto.ServerUsageBean;
import com.leadingsoft.bizfuse.cloud.saas.server.model.QServerInstance;
import com.leadingsoft.bizfuse.cloud.saas.server.model.QTenantServerConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.model.ServerInstance;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.ServerInstanceRepositoryCustom;
import com.leadingsoft.bizfuse.common.jpa.repository.AbstractRepository;
import com.leadingsoft.bizfuse.common.web.support.Searchable;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;

public class ServerInstanceRepositoryImpl extends AbstractRepository implements ServerInstanceRepositoryCustom {

    @Override
    public Page<ServerInstance> search(final Pageable pageable, final Searchable searchable) {
        final QServerInstance qServer = QServerInstance.serverInstance;
        final BooleanBuilder where = new BooleanBuilder();
        // 服务类型过滤
        where.and(super.equalsStr(qServer.type, searchable, "type"));
        final JPAQuery<ServerInstance> query = this.query().selectFrom(qServer).where(where);
        return this.search(query, pageable);
    }

    @Override
    public Page<ServerUsageBean> searchUsage(final Pageable pageable, final Searchable searchable) {
        final QServerInstance qServer = QServerInstance.serverInstance;
        final QTenantServerConfig qTenantServer = QTenantServerConfig.tenantServerConfig;
        final BooleanBuilder where = new BooleanBuilder();
        // 服务类型过滤
        where.and(super.equalsStr(qServer.type, searchable, "type"));

        final JPAQuery<ServerUsageBean> query = this.query()
                .select(Projections.bean(ServerUsageBean.class,
                        qServer.id,
                        qServer.type,
                        qServer.internalIP,
                        qServer.port,
                        qTenantServer.count().as("tenantCount")))
                .from(qTenantServer).rightJoin(qTenantServer.serverInstance, qServer)
                .where(where)
                .groupBy(qServer.id)
                .orderBy(qTenantServer.count().asc());
        return this.search(query, pageable);
    }

    @Override
    protected Class<?> getModelClass() {
        return ServerInstance.class;
    }

}
