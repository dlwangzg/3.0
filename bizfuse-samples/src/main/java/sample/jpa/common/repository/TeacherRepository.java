package sample.jpa.common.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import sample.jpa.common.model.Teacher;

public interface TeacherRepository extends Repository<Teacher, Long> {

    Teacher save(Teacher model);

    Teacher findOne(Long id);

    List<Teacher> findAll();
}
