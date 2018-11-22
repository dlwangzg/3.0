package sample.jpa.common.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStudent is a Querydsl query type for Student
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QStudent extends EntityPathBase<Student> {

    private static final long serialVersionUID = -1333176950L;

    public static final QStudent student = new QStudent("student");

    public final com.leadingsoft.bizfuse.common.jpa.model.QAbstractAuditModel _super = new com.leadingsoft.bizfuse.common.jpa.model.QAbstractAuditModel(this);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.util.Date> createdDate = _super.createdDate;

    public final NumberPath<Integer> grade = createNumber("grade", Integer.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<java.util.Date> lastModifiedDate = _super.lastModifiedDate;

    public final NumberPath<java.math.BigDecimal> money = createNumber("money", java.math.BigDecimal.class);

    public final StringPath name = createString("name");

    public final ListPath<Teacher, QTeacher> teachers = this.<Teacher, QTeacher>createList("teachers", Teacher.class, QTeacher.class, PathInits.DIRECT2);

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QStudent(String variable) {
        super(Student.class, forVariable(variable));
    }

    public QStudent(Path<? extends Student> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStudent(PathMetadata metadata) {
        super(Student.class, metadata);
    }

}

