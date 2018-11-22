package com.leadingsoft.bizfuse.base.uap.controller;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leadingsoft.bizfuse.base.uap.model.User;
import com.leadingsoft.bizfuse.base.uap.repository.UserRepository;
import com.leadingsoft.bizfuse.base.uap.service.UserService;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;
import com.leadingsoft.bizfuse.common.web.utils.encode.PasswordEncoder;
import com.leadingsoft.bizfuse.common.webauth.annotation.CurrentUser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 移动端的个人用户功能接口 (专门针对移动端节约流量设计的接口)
 */
@Slf4j
@RestController
@RequestMapping("/m/user")
public class UserMController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * 设置用户的登录名称
	 * <p>
	 * 仅当 loginId 为空时可设置
	 */
	@RequestMapping(value = "/setLoginId/{loginId}", method = RequestMethod.PUT)
	public ResultDTO<Void> setLoginId(@PathVariable String loginId, @CurrentUser final String userNo) {
		this.userService.setUserLoginId(userNo, loginId);
		log.info(String.format("设置账户 [%s] 的 loginId [ %s ]", userNo, loginId));
		return ResultDTO.success();
	}

	/**
	 * 绑定用户的手机号码
	 */
	@RequestMapping(value = "/bindMobile/{mobile}", method = RequestMethod.PUT)
	public ResultDTO<Void> bindMobile(@PathVariable String mobile, @CurrentUser final String userNo) {
		this.userService.bindUserMobile(userNo, mobile);
		log.info(String.format("账户 [%s] 绑定手机号 [ %s ]", userNo, mobile));
		return ResultDTO.success();
	}

	/**
	 * 绑定用户的电子邮箱
	 */
	@RequestMapping(value = "bindEmail", method = RequestMethod.PUT)
	public ResultDTO<Void> bindEmail(@CurrentUser String userNo, @RequestParam String email) {
		this.userService.bindUserEmail(userNo, email);
		log.info(String.format("账户 [%s] 绑定邮箱 [ %s ]", userNo, email));
		return ResultDTO.success();
	}

	/**
	 * 修改密码
	 * 
	 * @return
	 */
	public ResultDTO<Void> changePassword(@CurrentUser String userNo, @RequestBody @Valid ChangePasswordDTO dto) {
		if (!dto.getPassword().equals(dto.getConfirmPassword())) {
			throw new CustomRuntimeException("400", "密码与确认密码不符");
		}
		User user = this.userRepository.findOneByNo(userNo);
		if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
			throw new CustomRuntimeException("400", "原密码错误");
		}
		this.userService.changeUserPassword(userNo, dto.getPassword());
		log.info(String.format("账户 [%s] 修改密码成功", userNo));
		return ResultDTO.success();
	}

	@Getter
	public static class ChangePasswordDTO {
		/**
		 * 新密码
		 */
		@NotBlank
		private String password;

		/**
		 * 确认密码
		 */
		@NotBlank
		private String confirmPassword;

		/**
		 * 当前密码
		 */
		@NotBlank
		private String currentPassword;
	}
}
