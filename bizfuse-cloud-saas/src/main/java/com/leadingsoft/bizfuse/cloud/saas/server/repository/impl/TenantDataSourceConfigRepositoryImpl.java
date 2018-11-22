package com.leadingsoft.bizfuse.cloud.saas.server.repository.impl;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.cloud.saas.dto.TenantDataSourceSyncBean;
import com.leadingsoft.bizfuse.cloud.saas.server.model.QTenantDataSourceConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.model.TenantDataSourceConfig;
import com.leadingsoft.bizfuse.cloud.saas.server.repository.TenantDataSourceConfigRepositoryCustom;
import com.leadingsoft.bizfuse.common.jpa.repository.AbstractRepository;
import com.leadingsoft.bizfuse.common.web.support.Searchable;
import com.querydsl.core.BooleanBuilder;

@Component
public class TenantDataSourceConfigRepositoryImpl extends AbstractRepository
        implements TenantDataSourceConfigRepositoryCustom {

    @Override
    public Page<TenantDataSourceConfig> page(final Pageable pageable, final Searchable searchable) {
        final QTenantDataSourceConfig qTSC = QTenantDataSourceConfig.tenantDataSourceConfig;
        final BooleanBuilder where = new BooleanBuilder();

        // 租户过滤
        where.and(super.equalsStr(qTSC.tenantNo, searchable, "tenantNo"))
                // 服务类型过滤
                .and(super.equalsStr(qTSC.serverType, searchable, "serverType"));
        return super.search(where, pageable, qTSC);
    }

    @Override
    public List<TenantDataSourceConfig> search(final TenantDataSourceSyncBean search) {
        final QTenantDataSourceConfig qTSC = QTenantDataSourceConfig.tenantDataSourceConfig;
        final BooleanBuilder where = new BooleanBuilder();
        if ((search.getTenantNos() != null) && (search.getTenantNos().size() > 0)) {
            where.and(qTSC.tenantNo.in(search.getTenantNos()));
        }
        if (search.getServerType() != null) {
            where.and(qTSC.serverType.eq(search.getServerType()));
        }
        if (search.getLastModifiedTime() != null) {
            where.and(qTSC.lastModifiedDate.after(new Date(search.getLastModifiedTime())));
        }
        return this.query().selectFrom(qTSC).where(where).fetch();
    }

    @Override
    protected Class<?> getModelClass() {
        return TenantDataSourceConfig.class;
    }

}
