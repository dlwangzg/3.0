package com.leadingsoft.bizfuse.base.uap.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.leadingsoft.bizfuse.common.jpa.model.AbstractModel;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户登录认证日志
 * 
 * @author liuyg
 *
 */
@Getter
@Setter
@Entity
public class AuthenticationLog extends AbstractModel {

	private static final long serialVersionUID = 2482503933387521848L;

	// 认证结果
	public static enum Result {
		FAILURE, SUCCESS
	}

	// 认证渠道类型
	public static enum AuthChannel {
		// 微信，账号， token
		weixin, account, token;
	}

	/**
	 * 认证结果（认证成功|认证失败）
	 */
	@Enumerated
	@Column(nullable = false)
	private Result result;

	/**
	 * 登录用户标识（编号）
	 */
	@Column(nullable = false)
	private String userNo;

	/**
	 * 认证渠道
	 */
	@Enumerated
	private AuthChannel channel;

    /**
     * 用户的远程IP
     */
	private String remoteAddress;

	/**
	 * SessionID
	 */
	private String sessionId;
	
	/**
	 * 创建时间
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdTime;
}
