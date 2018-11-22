package com.leadingsoft.bizfuse.base.dict.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.base.dict.model.DictionaryCategory;

public interface DictionaryCategoryRepository extends Repository<DictionaryCategory, Long> {

    DictionaryCategory findOne(Long id);

    DictionaryCategory findOneByKey(String key);

    Page<DictionaryCategory> findAll(Pageable pageable);

    DictionaryCategory save(DictionaryCategory model);

    void deleteAll();

    List<DictionaryCategory> findAll();

    Page<DictionaryCategory> findAllByKeyContaining(String key, Pageable pageable);

    Page<DictionaryCategory> findAllByDescriptionContaining(String description, Pageable pageable);
}
