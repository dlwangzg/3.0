package com.leadingsoft.bizfuse.base.uap.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leadingsoft.bizfuse.base.uap.dto.AuthenticationDTO;
import com.leadingsoft.bizfuse.base.uap.dto.AuthenticationUserDTO;
import com.leadingsoft.bizfuse.base.uap.model.AuthenticationLog;
import com.leadingsoft.bizfuse.base.uap.model.NonceAuthenticationToken;
import com.leadingsoft.bizfuse.base.uap.model.User;
import com.leadingsoft.bizfuse.base.uap.model.AuthenticationLog.Result;
import com.leadingsoft.bizfuse.base.uap.repository.AuthenticationLogRepository;
import com.leadingsoft.bizfuse.base.uap.repository.UserRepository;
import com.leadingsoft.bizfuse.base.uap.service.NonceAuthenticationTokenService;
import com.leadingsoft.bizfuse.base.uap.service.UserService;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;
import com.leadingsoft.bizfuse.common.web.utils.encode.PasswordEncoder;

@RestController
public class AuthenticationController {

	@Autowired
	private UserService userService;

	@Autowired
	private NonceAuthenticationTokenService nonceAuthenticationTokenService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthenticationLogRepository authenticationLogRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResultDTO<AuthenticationUserDTO> authenticate(@RequestBody final AuthenticationDTO dto) {
		AuthenticationUserDTO authUserDTO = null;
		if (this.isNonceTokenLogin(dto)) {// 处理自动登录
			authUserDTO = this.authenticateByNonceToken(dto);
		} else if (this.isAccountLogin(dto)) {// 账号登录
			authUserDTO = this.authenticateByUserAccount(dto);
		} else {// 微信登录
			authUserDTO = this.authenticateByWeChatAccount(dto);
		}
		if (authUserDTO == null) {
			createAuthLog(dto, null);
			throw new CustomRuntimeException("401", "用户不存在");
		}
		if (dto.needNonceToken()) {
			// 生成新的自动登录Token
			final String nonceToken = this.createNonceToken(dto, authUserDTO.getNo());
			authUserDTO.setNonceToken(nonceToken);
		}
		createAuthLog(dto, authUserDTO);
		final ResultDTO<AuthenticationUserDTO> rs = ResultDTO.success(authUserDTO);
		return rs;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.DELETE)
	public ResultDTO<?> logout(@RequestParam(required=false) final String nonceToken) {
		if (nonceToken != null) {
			this.nonceAuthenticationTokenService.deleteNonceToken(nonceToken);
		}
		return ResultDTO.success();
	}

	private AuthenticationUserDTO authenticateByNonceToken(final AuthenticationDTO dto) {
		final User user = this.nonceAuthenticationTokenService.authenticate(dto.getNonceToken(), dto.getDeviceId(),
				dto.getDeviceType(), dto.getOsType(), dto.getOsVersion(), dto.getSoftwareType(),
				dto.getSoftwareVersion());
		return this.toDTO(user);
	}

	private String createNonceToken(final AuthenticationDTO dto, final String userNo) {
		final NonceAuthenticationToken token = new NonceAuthenticationToken();
		token.setNo(userNo);
		token.setDeviceId(dto.getDeviceId());
		token.setDeviceType(dto.getDeviceType());
		token.setOsType(dto.getOsType());
		token.setOsVersion(dto.getOsVersion());
		token.setSoftwareType(dto.getSoftwareType());
		token.setSoftwareVersion(dto.getSoftwareVersion());
		return this.nonceAuthenticationTokenService.createNonceToken(token);
	}

	/**
	 * 用户的微信账户认证
	 *
	 * @param dto
	 * @return
	 */
	private AuthenticationUserDTO authenticateByWeChatAccount(final AuthenticationDTO dto) {
		User model = null;
		if (StringUtils.hasText(dto.getUnionId())) {
			model = this.userRepository.findOneByUnionId(dto.getUnionId());
		}
		if (model != null) {
			return this.toDTO(model);
		}
		if (StringUtils.hasText(dto.getWebsiteAppOpenId())) {
			model = this.userRepository.findOneByWebsiteAppOpenId(dto.getWebsiteAppOpenId());
		} else if (StringUtils.hasText(dto.getSubscriptionOpenId())) {
			model = this.userRepository.findOneBySubscriptionOpenId(dto.getSubscriptionOpenId());
		} else if (StringUtils.hasText(dto.getMobileAppOpenId())) {
			model = this.userRepository.findOneByMobileAppOpenId(dto.getMobileAppOpenId());
		}
		// TODO: 如果微信号未注册，新注册一个临时账户
		return this.toDTO(model);
	}

	/**
	 * 用户账户认证（用户名／手机号／邮件地址＋密码认证）
	 *
	 * @param dto
	 * @return
	 */
	private AuthenticationUserDTO authenticateByUserAccount(final AuthenticationDTO dto) {
		String username = dto.getUsername();
		if (StringUtils.hasText(username)) {
			username = username.trim();
		}
		final User model = this.userService.findUserByIdentity(username);
		if (model == null || !passwordEncoder.matches(dto.getPassword(), model.getPassword())) {
			throw new CustomRuntimeException("401", "用户名或密码错误.");
		}
		if (model.isAccountExpired()) {
			throw new CustomRuntimeException("401", "账户已过期.");
		}
		if (model.isAccountLocked()) {
			throw new CustomRuntimeException("401", "账户已锁定，请稍后重试.");
		}
		if (!model.isEnabled()) {
			throw new CustomRuntimeException("401", "账户已禁用.");
		}
		return this.toDTO(model);
	}

	private boolean isAccountLogin(final AuthenticationDTO dto) {
		return StringUtils.hasText(dto.getUsername());
	}

	private boolean isNonceTokenLogin(final AuthenticationDTO dto) {
		return StringUtils.hasText(dto.getNonceToken());
	}

	/**
	 * 认证DTO转换仅在该Controller使用，不具有通用性，故而没有实现Convertor的统一方式
	 *
	 * @param model
	 * @return
	 */
	private AuthenticationUserDTO toDTO(final User model) {
		if (model == null) {
			return null;
		}
		final AuthenticationUserDTO dto = new AuthenticationUserDTO();
		dto.setNo(model.getNo());

		dto.setLoginId(model.getLoginId());
		dto.setMobile(model.getMobile());
		dto.setEmail(model.getEmail());
		dto.setPassword(model.getPassword());
		dto.setName(model.getDetails().getName());
		dto.setNickname(model.getDetails().getNickname());

		dto.setUnionId(model.getUnionId());
		dto.setSubscriptionOpenId(model.getSubscriptionOpenId());
		dto.setMobileAppOpenId(model.getMobileAppOpenId());
		dto.setWebsiteAppOpenId(model.getWebsiteAppOpenId());

		dto.setEnabled(model.isEnabled());
		dto.setAccountLocked(model.isAccountLocked());
		dto.setAccountExpired(model.isAccountExpired());
		dto.setCredentialsExpired(model.isCredentialsExpired());
		return dto;
	}
	

	private void createAuthLog(AuthenticationDTO dto, AuthenticationUserDTO authUserDTO) {
		AuthenticationLog log = new AuthenticationLog();
		if (authUserDTO == null) {
			// 认证失败
			log.setResult(Result.FAILURE);
		} else {
			log.setResult(Result.SUCCESS);
			log.setUserNo(dto.getUsername());
		}
		log.setCreatedTime(new Date());
		authenticationLogRepository.save(log);
	}
}
