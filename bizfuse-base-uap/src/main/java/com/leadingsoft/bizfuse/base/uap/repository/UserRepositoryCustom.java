package com.leadingsoft.bizfuse.base.uap.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.leadingsoft.bizfuse.base.uap.model.User;
import com.leadingsoft.bizfuse.common.web.support.Searchable;

public interface UserRepositoryCustom {

	Page<User> searchAll(Searchable searchable, Pageable pageable);
	
	List<User> searchAll(Searchable searchable);
}
