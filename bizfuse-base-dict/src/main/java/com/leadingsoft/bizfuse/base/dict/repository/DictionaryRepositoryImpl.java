package com.leadingsoft.bizfuse.base.dict.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.base.dict.model.Dictionary;
import com.leadingsoft.bizfuse.base.dict.model.QDictionary;
import com.leadingsoft.bizfuse.common.jpa.repository.AbstractRepository;
import com.leadingsoft.bizfuse.common.web.support.Searchable;
import com.querydsl.core.BooleanBuilder;

@Component
public class DictionaryRepositoryImpl extends AbstractRepository implements DictionaryRepositoryCustom {

    @Override
    public Page<Dictionary> findAllByCategoryId(final Long categoryId, final Searchable searchable,
            final Pageable pageable) {
        final QDictionary qModel = QDictionary.dictionary;
        final BooleanBuilder where = new BooleanBuilder();
        where.and(qModel.category.id.eq(categoryId));
        if (searchable.hasKey("key")) {
            final String searchKey = searchable.getStrValue("key");
            where.and(qModel.key.contains(searchKey));
        }
        if (searchable.hasKey("discarded")) {
            final Boolean discarded = searchable.getBooleanValue("discarded");
            where.and(qModel.discarded.eq(discarded));
        }
        if (pageable.getSort() == null) {
            // 默认照层级和sortIndex排序
            return this.search(where, pageable, qModel, qModel.parent.id.asc(), qModel.sortIndex.asc());
        }
        return this.search(where, pageable, qModel);
    }

    @Override
    protected Class<?> getModelClass() {
        return Dictionary.class;
    }

}
