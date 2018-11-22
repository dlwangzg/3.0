package com.leadingsoft.bizfuse.base.authority.model.authorization;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.validator.constraints.NotBlank;

import com.leadingsoft.bizfuse.base.authority.model.role.Role;
import com.leadingsoft.bizfuse.common.jpa.model.AbstractAuditModel;

/**
 * 用户授予的角色
 * <p>
 * 给用户分配的角色
 * 
 * @author liuyg
 *
 */
@Entity
public class UserGrantedRole extends AbstractAuditModel {

	private static final long serialVersionUID = 3785915510693182708L;

	@NotBlank
	private String userNo;

	@ManyToOne
	private Role role;

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
