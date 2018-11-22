package com.leadingsoft.bizfuse.base.dict.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.leadingsoft.bizfuse.base.dict.model.Dictionary;
import com.leadingsoft.bizfuse.common.web.support.Searchable;

public interface DictionaryRepositoryCustom {

    Page<Dictionary> findAllByCategoryId(Long categoryId, Searchable searchable, Pageable pageable);
}
