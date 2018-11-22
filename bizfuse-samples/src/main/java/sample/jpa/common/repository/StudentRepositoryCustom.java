package sample.jpa.common.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.leadingsoft.bizfuse.common.web.support.Searchable;

import sample.jpa.common.model.Student;

public interface StudentRepositoryCustom {

    Page<Student> searchPage(Pageable pageable, Searchable searchable);

    List<Student> searchAll(Searchable searchable);
}
