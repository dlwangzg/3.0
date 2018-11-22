package sample.jpa.common.model;

import javax.persistence.Entity;

import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.common.jpa.model.AbstractAuditModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Teacher extends AbstractAuditModel {

    private static final long serialVersionUID = -963992146469620117L;

    @NotBlank
    private String name;

    private int age;
}
