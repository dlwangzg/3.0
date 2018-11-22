package com.leadingsoft.bizfuse.base.uap.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leadingsoft.bizfuse.base.uap.dto.UserDTO;
import com.leadingsoft.bizfuse.base.uap.dto.convertor.UserConvertor;
import com.leadingsoft.bizfuse.base.uap.model.User;
import com.leadingsoft.bizfuse.base.uap.repository.UserRepository;
import com.leadingsoft.bizfuse.base.uap.service.UserService;
import com.leadingsoft.bizfuse.common.web.dto.result.PageResultDTO;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;
import com.leadingsoft.bizfuse.common.webauth.annotation.CurrentUser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Web端的用户管理功能接口
 */
@Slf4j
@RestController
@RequestMapping("/w/users")
public class UserWController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserConvertor userConvertor;

	@Autowired
	private UserRepository userRepository;

	/**
	 * 列举所有用户的分页数据
	 */
	@RequestMapping(method = RequestMethod.GET)
	public PageResultDTO<UserDTO> page(final Pageable pageable) {
		final Page<User> pageModel = this.userRepository.findAll(pageable);

		return this.userConvertor.toResultDTO(pageModel);
	}

	/**
	 * 创建用户
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResultDTO<UserDTO> create(@RequestBody final UserDTO dto) {
		final User paramModel = this.userConvertor.toModel(dto);
		final User savedModel = this.userService.createUser(paramModel);
		log.info(String.format("新建用户 [ %s ]", savedModel.getNo()));
		final ResultDTO<UserDTO> resultDTO = this.userConvertor.toResultDTO(savedModel);
		return resultDTO;
	}

	/**
	 * 查询用户信息
	 */
	@RequestMapping(value = "/{no}", method = RequestMethod.GET)
	public ResultDTO<UserDTO> get(@PathVariable("no") final String no) {
		final User model = this.userRepository.findOneByNo(no);

		final ResultDTO<UserDTO> resultDTO = this.userConvertor.toResultDTO(model);
		return resultDTO;
	}

	/**
	 * 修改用户信息
	 * <p>
	 * 此处不允许修改惟一性字段（登录名称、手机号码、电子邮箱）及登录密码
	 */
	@RequestMapping(value = "/{no}", method = RequestMethod.PUT)
	public ResultDTO<UserDTO> update(@PathVariable("no") final String no, @RequestBody final UserDTO dto) {
		dto.setNo(no);

		final User paramModel = this.userConvertor.toModel(dto);
		final User savedModel = this.userRepository.save(paramModel);

		final ResultDTO<UserDTO> resultDTO = this.userConvertor.toResultDTO(savedModel);
		return resultDTO;
	}

	/**
	 * 设置用户的登录名称
	 * <p>
	 * 仅当 loginId 为空时可设置
	 */
	@RequestMapping(value = "/{no}/setLoginId", method = RequestMethod.PUT)
	public ResultDTO<UserDTO> changeLoginId(@PathVariable("no") final String no,
			@RequestBody @Valid final ChangeLoginIdDTO dto) {
		this.userService.setUserLoginId(no, dto.getLoginId());

		return this.userConvertor.toResultDTO(this.userRepository.findOneByNo(no));
	}

	/**
	 * 修改用户的手机号码
	 */
	@RequestMapping(value = "/{no}/changeMobile", method = RequestMethod.PUT)
	public ResultDTO<UserDTO> changeMobile(@PathVariable("no") final String no,
			@RequestBody @Valid final ChangeMobileDTO dto) {
		final User savedModel = this.userService.bindUserMobile(no, dto.getMobile());

		final ResultDTO<UserDTO> resultDTO = this.userConvertor.toResultDTO(savedModel);
		return resultDTO;
	}

	/**
	 * 修改用户的电子邮箱
	 */
	@RequestMapping(value = "/{no}/changeEmail", method = RequestMethod.PUT)
	public ResultDTO<UserDTO> changeEmail(@PathVariable("no") final String no,
			@RequestBody @Valid final ChangeEmailDTO dto) {
		final User savedModel = this.userService.bindUserEmail(no, dto.getEmail());

		final ResultDTO<UserDTO> resultDTO = this.userConvertor.toResultDTO(savedModel);
		return resultDTO;
	}

	/**
	 * 修改用户的登录密码
	 */
	@RequestMapping(value = "/{no}/changePassword", method = RequestMethod.PUT)
	public ResultDTO<UserDTO> changePassword(@PathVariable("no") final String no,
			@RequestBody @Valid final ChangePasswordDTO dto) {
		this.userService.changeUserPassword(no, dto.getPassword());

		final ResultDTO<UserDTO> resultDTO = this.userConvertor.toResultDTO(userRepository.findOneByNo(no));
		return resultDTO;
	}

	/**
	 * 删除用户帐号
	 */
	@RequestMapping(value = "/{no}", method = RequestMethod.DELETE)
	public ResultDTO<Void> delete(@PathVariable("no") @NotBlank final String no,
			@CurrentUser @NotNull final User user) {
		// 防止将自身用户帐号删掉
		if (user.getNo().equals(no)) {
			throw new CustomRuntimeException("不允许删除自身用户帐号");
		}

		this.userService.removeUserByNo(no);

		return ResultDTO.success();
	}

	/**
	 * 检查是否手机号已被注册
	 */
	@RequestMapping(value = "/check/mobileRegistered", method = RequestMethod.GET)
	public ResultDTO<Boolean> isMobileRegistered(@RequestParam String mobile) {
		User user = this.userRepository.findOneByMobile(mobile);
		return ResultDTO.success(user != null);
	}

	/**
	 * 检查是否LoginId已被注册
	 */
	@RequestMapping(value = "/check/loginIdRegistered", method = RequestMethod.GET)
	public ResultDTO<Boolean> isLoginIdRegistered(@RequestParam String loginId) {
		User user = this.userRepository.findOneByLoginId(loginId);
		return ResultDTO.success(user != null);
	}

	/**
	 * 检查是否邮箱已被注册
	 */
	@RequestMapping(value = "/check/emailRegistered", method = RequestMethod.GET)
	public ResultDTO<Boolean> isEmailRegistered(@RequestParam String email) {
		User user = this.userRepository.findOneByEmail(email);
		return ResultDTO.success(user != null);
	}

	@Getter
	private final static class ChangeLoginIdDTO {
		@NotBlank
		private String loginId;
	}

	@Getter
	private final static class ChangeMobileDTO {
		@NotBlank
		private String mobile;
	}

	@Getter
	private final static class ChangeEmailDTO {
		@NotBlank
		private String email;
	}

	@Getter
	private final static class ChangePasswordDTO {
		@NotBlank
		private String password;
	}
}
