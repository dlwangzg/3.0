package sample.jpa.common.repository;

import java.util.List;

import org.springframework.data.repository.Repository;

import sample.jpa.common.model.Student;

public interface StudentRepository extends Repository<Student, Long>, StudentRepositoryCustom {

    Student save(Student model);

    Student findOne(Long id);

    List<Student> findAll();
}
