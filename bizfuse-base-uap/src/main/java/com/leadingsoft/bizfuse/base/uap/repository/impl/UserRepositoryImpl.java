package com.leadingsoft.bizfuse.base.uap.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.leadingsoft.bizfuse.base.uap.model.QUser;
import com.leadingsoft.bizfuse.base.uap.model.User;
import com.leadingsoft.bizfuse.base.uap.repository.UserRepositoryCustom;
import com.leadingsoft.bizfuse.common.jpa.repository.AbstractRepository;
import com.leadingsoft.bizfuse.common.web.support.Searchable;
import com.querydsl.core.BooleanBuilder;

public class UserRepositoryImpl extends AbstractRepository implements UserRepositoryCustom {

    @Override
    public Page<User> searchAll(final Searchable searchable, final Pageable pageable) {
        return this.search(this.searchCondition(searchable), pageable, QUser.user);
    }

    @Override
    public List<User> searchAll(final Searchable searchable) {
        return this.queryFactory.selectFrom(QUser.user).where(this.searchCondition(searchable)).fetch();
    }

    private BooleanBuilder searchCondition(final Searchable searchable) {
        final QUser qUser = QUser.user;
        final BooleanBuilder where = new BooleanBuilder();
        if (searchable.hasKey("mobile")) {
            where.and(qUser.mobile.eq(searchable.getStrValue("mobile")));
        } else if (searchable.hasKey("loginId")) {
            where.and(qUser.loginId.eq(searchable.getStrValue("loginId")));
        } else if (searchable.hasKey("email")) {
            where.and(qUser.email.eq(searchable.getStrValue("email")));
        } else if (searchable.hasKey("unionId")) {
            where.and(qUser.unionId.eq(searchable.getStrValue("unionId")));
        } else if (searchable.hasKey("name")) {
            where.and(qUser.details.name.contains(searchable.getStrValue("name")));
        }
        return where;
    }

    @Override
    protected Class<?> getModelClass() {
        return User.class;
    }

}
