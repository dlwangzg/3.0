package sample.jpa.common.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.leadingsoft.bizfuse.common.jpa.model.AbstractAuditModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Student extends AbstractAuditModel {

	private static final long serialVersionUID = -8272200930143261633L;

	@NotBlank
	private String name;

	private int grade;

	@Column(columnDefinition = "DECIMAL(17,6)")
	private BigDecimal money;

	@JsonIgnore
	@ManyToMany
	private List<Teacher> teachers = new ArrayList<>();
}
