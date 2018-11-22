package com.leadingsoft.bizfuse.base.uap.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.leadingsoft.bizfuse.base.uap.dto.AccountRegisterDTO;
import com.leadingsoft.bizfuse.base.uap.dto.MobileRegisterDTO;
import com.leadingsoft.bizfuse.base.uap.dto.UserDTO;
import com.leadingsoft.bizfuse.base.uap.dto.WeChatRegisterDTO;
import com.leadingsoft.bizfuse.base.uap.dto.convertor.UserConvertor;
import com.leadingsoft.bizfuse.base.uap.model.User;
import com.leadingsoft.bizfuse.base.uap.service.UserService;
import com.leadingsoft.bizfuse.common.web.dto.result.ResultDTO;
import com.leadingsoft.bizfuse.common.webauth.annotation.CurrentUser;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户注册接口
 */
@Slf4j
@RestController
public class UserRegisterController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserConvertor userConvertor;

	/**
	 * 根据微信账号注册账户
	 */
	@RequestMapping(value = "/wechat/register", method = RequestMethod.POST)
	public ResultDTO<UserDTO> registerByWeChat(@RequestBody @Valid final WeChatRegisterDTO dto) {
		final User model = this.userService.registerUserByWeChat(dto.getOpenId(), dto.getUnionId(), dto.getChannel());
		log.info(String.format("微信账号注册账户成功， 账户编号 [ %s ] ", model.getNo()));
		final ResultDTO<UserDTO> resultDTO = this.userConvertor.toResultDTO(model);
		return resultDTO;
	}

	/**
	 * 根据手机号注册账户
	 */
	@RequestMapping(value = "/mobile/register", method = RequestMethod.POST)
	public ResultDTO<UserDTO> registerByMobile(@RequestBody @Valid final MobileRegisterDTO dto) {
		final User model = this.userService.registerUserByMobile(dto.getMobile(), dto.getPassword(), dto.getName(),
				dto.getNickname());
		log.info(String.format("手机号注册账户成功， 账户编号 [ %s ] ", model.getNo()));
		final ResultDTO<UserDTO> resultDTO = this.userConvertor.toResultDTO(model);
		return resultDTO;
	}

	/**
	 * 手机号、密码绑定到当前登录的微信注册账户
	 */
	@RequestMapping(value = "/mobile/register/bindMobilePasswd", method = RequestMethod.POST)
	public ResultDTO<UserDTO> bindMobilePasswd(@CurrentUser final String userNo,
			@RequestBody @Valid final MobileRegisterDTO dto) {
		final User model = this.userService.bindUserMobileAndPassword(userNo, dto.getMobile(), dto.getPassword());
		log.info(String.format("账户绑定手机号、密码成功， 账户编号 [ %s ]，手机号 [ %s ] ", model.getNo(), dto.getMobile()));
		final ResultDTO<UserDTO> resultDTO = this.userConvertor.toResultDTO(model);
		return resultDTO;
	}

	/**
	 * 根据用户名密码注册账户
	 */
	@RequestMapping(value = "/account/register", method = RequestMethod.POST)
	public ResultDTO<UserDTO> registerByAccount(@RequestBody @Valid final AccountRegisterDTO dto) {
		final User model = this.userService.registerUserByUsernamePassword(dto.getLoginId(), dto.getPassword(),
				dto.getName(), dto.getNickname());
		final ResultDTO<UserDTO> resultDTO = this.userConvertor.toResultDTO(model);
		return resultDTO;
	}
}
