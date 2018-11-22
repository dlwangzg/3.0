package com.leadingsoft.bizfuse.base.dict.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.base.dict.model.DictionarysVersion;

public interface DictionarysVersionRepository extends Repository<DictionarysVersion, Long> {

    List<DictionarysVersion> findAll();

    DictionarysVersion save(DictionarysVersion version);

}
