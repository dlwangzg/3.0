package sample.jpa.common.repository.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.leadingsoft.bizfuse.common.jpa.repository.AbstractRepository;
import com.leadingsoft.bizfuse.common.web.support.Searchable;
import com.querydsl.core.BooleanBuilder;

import sample.jpa.common.model.QStudent;
import sample.jpa.common.model.Student;
import sample.jpa.common.repository.StudentRepositoryCustom;

@Repository
public class StudentRepositoryImpl extends AbstractRepository implements StudentRepositoryCustom {

    private static final String NAME = "name";

    private final QStudent qStudent = QStudent.student;

    @Override
    public Page<Student> searchPage(final Pageable pageable, final Searchable searchable) {
        final BooleanBuilder where = this.createWhere(searchable);
        return this.search(where, pageable, this.qStudent);
    }

    @Override
    public List<Student> searchAll(final Searchable searchable) {
        final BooleanBuilder where = this.createWhere(searchable);
        return this.query().selectFrom(this.qStudent).where(where).fetch();
    }

    /**
     * 根据过滤条件, 生成Where语句
     *
     * @param searchable
     * @return
     */
    private BooleanBuilder createWhere(final Searchable searchable) {
        final BooleanBuilder where = new BooleanBuilder();
        if (searchable.hasKey(StudentRepositoryImpl.NAME)) {
            where.and(this.qStudent.name.contains(searchable.getStrValue(StudentRepositoryImpl.NAME)));
        }
        return where;
    }

    @Override
    protected Class<?> getModelClass() {
        return Student.class;
    }

}
