package com.leadingsoft.bizfuse.base.uap.service.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.leadingsoft.bizfuse.base.uap.dto.WeChatRegisterDTO.WeChatChannel;
import com.leadingsoft.bizfuse.base.uap.model.User;
import com.leadingsoft.bizfuse.base.uap.repository.UserRepository;
import com.leadingsoft.bizfuse.base.uap.service.IdGeneratorService;
import com.leadingsoft.bizfuse.base.uap.service.UserService;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;
import com.leadingsoft.bizfuse.common.web.utils.encode.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private IdGeneratorService idGeneratorService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User findUserByIdentity(String identity) {
		// 如果 identity 匹配用户的手机号码
		if (isMobile(identity)) {
			return this.userRepository.findOneByMobile(identity);
		}
		// 如果 identity 匹配用户的电子邮箱
		if (isEmail(identity)) {
			return this.userRepository.findOneByEmail(identity);
		}
		// 通过 identity 匹配用户的登录名称
		return this.userRepository.findOneByLoginId(identity);
	}

	@Override
	public User createUser(User user) {
		// 校验是否已指定 id
		if (user.getId() != null) {
			throw new CustomRuntimeException("创建用户不允许指定用户ID[id]");
		}

		// 校验是否已指定 no
		if (StringUtils.hasText(user.getNo())) {
			throw new CustomRuntimeException("创建用户不允许指定用户编号[no]");
		}

		// 校验 loginId 的惟一性
		final String loginId = user.getLoginId();
		if (StringUtils.hasText(loginId)) {
			if (this.userRepository.findOneByLoginId(loginId) != null) {
				throw new CustomRuntimeException(String.format("用户登录名称[%s]已存在", loginId));
			}
		}

		// 校验 mobile 的惟一性
		final String mobile = user.getMobile();
		if (StringUtils.hasText(mobile)) {
			if (this.userRepository.findOneByMobile(mobile) != null) {
				throw new CustomRuntimeException(String.format("用户手机号码[%s]已存在", mobile));
			}
		}

		// 校验 email 的惟一性
		final String email = user.getEmail();
		if (StringUtils.hasText(email)) {
			if (this.userRepository.findOneByEmail(email) != null) {
				throw new CustomRuntimeException(String.format("用户电子邮箱[%s]已存在", email));
			}
		}

		// 校验登录标识 loginId/mobile/email 的有效性
		if (!StringUtils.hasText(loginId) && !StringUtils.hasText(mobile) && !StringUtils.hasText(email)) {
			throw new CustomRuntimeException("必须指定用户登录名称[loginId], 或手机号码[mobile], 或电子邮箱[email]");
		}

		// 校验 password 的有效性
		final String password = user.getPassword();
		if (!StringUtils.hasText(password)) {
			throw new CustomRuntimeException("必须指定用户登录密码[password]");
		}

		user.setNo(this.idGeneratorService.generateUserNo());

		return this.userRepository.save(user);
	}

	@Override
	public User registerUserByUsernamePassword(String username, String password, String name, String nickname) {
		if (!StringUtils.hasText(username)) {
			throw new CustomRuntimeException("必须指定用户名[username]");
		}
		final User existing = this.userRepository.findOneByLoginId(username);
		if (existing != null) {
			throw new CustomRuntimeException(String.format("用户名[%s]已存在", username));
		}
		final User user = new User();
		user.setNo(this.idGeneratorService.generateUserNo());
		user.setLoginId(username);
		user.setPassword(this.passwordEncoder.encode(password));
		user.getDetails().setName(name);
		user.getDetails().setNickname(nickname);
		user.setEnabled(true);
		this.userRepository.save(user);
		return user;
	}

	@Override
	public User registerUserByWeChat(String openId, String unionId, WeChatChannel channel) {
		// 已经通过其他微信渠道注册过的，绑定openId
		User user = this.userRepository.findOneByUnionId(unionId);
		if (user != null) {
			return this.bindOpenId(user, openId, channel);
		}
		switch (channel) {
		case wechatWebsite:
			user = this.registerByWebsitApp(unionId, openId);
			break;
		case wechatMobileApp:
			user = this.registerByMobileApp(unionId, openId);
			break;
		case officialAccount:
			user = this.registerBySubscription(unionId, openId);
			break;
		default:
			throw new RuntimeException("未处理的渠道类型：" + channel);
		}
		return user;
	}

	@Override
	public User registerUserByEmail(String email, String password, String name, String nickname) {
		if (!StringUtils.hasText(email)) {
			throw new CustomRuntimeException("必须指定邮件地址[email]");
		}
		final User existingUser = this.userRepository.findOneByEmail(email);
		if (existingUser != null) {
			throw new CustomRuntimeException("406", String.format("邮件地址[%s]已经被注册.", email));
		}
		final User user = new User();
		user.setNo(this.idGeneratorService.generateUserNo());
		user.setEmail(email);
		user.setPassword(this.passwordEncoder.encode(password));
		user.setEnabled(false);
		user.getDetails().setName(name);
		user.getDetails().setNickname(nickname);
		this.userRepository.save(user);
		// TODO:生成邮箱激活码，并发送给注册的邮件地址
		// 等待用户激活邮箱，并设置账户enabled为true
		return user;
	}

	@Override
	public User registerUserByMobile(String mobile, String password, String name, String nickname) {
		if (!StringUtils.hasText(mobile)) {
			throw new CustomRuntimeException("必须指定用户手机号码[mobile]");
		}
		final User existing = this.userRepository.findOneByMobile(mobile);
		if (existing != null) {
			throw new CustomRuntimeException(String.format("用户手机号码[%s]已存在", mobile));
		}
		final User user = new User();
		user.setNo(this.idGeneratorService.generateUserNo());
		user.setMobile(mobile);
		user.setPassword(this.passwordEncoder.encode(password));
		user.getDetails().setName(name);
		user.getDetails().setNickname(nickname);
		user.setEnabled(true);
		this.userRepository.save(user);
		return user;
	}

	@Override
	public User bindUserMobileAndPassword(String no, String mobile, String password) {
		final User user = this.userRepository.findOneByNo(no);
		if (StringUtils.hasText(user.getMobile())) {
			throw new CustomRuntimeException(String.format("该账户已绑定手机号码[%s]，请重新登录", mobile));
		}

		// 如果该手机号已经注册，保留原账户，微信信息合并到原账号，删除微信注册的账号
		final User mobileRegistedUser = this.userRepository.findOneByMobile(mobile);
		if (mobileRegistedUser != null) {
			if (user.getWeChatId() != null) {
				mobileRegistedUser.setWeChatId(user.getWeChatId());
			}
			if (user.getWebsiteAppOpenId() != null) {
				mobileRegistedUser.setWebsiteAppOpenId(user.getWebsiteAppOpenId());
			}
			if (user.getMobileAppOpenId() != null) {
				mobileRegistedUser.setMobileAppOpenId(user.getMobileAppOpenId());
			}
			if (user.getSubscriptionOpenId() != null) {
				mobileRegistedUser.setSubscriptionOpenId(user.getSubscriptionOpenId());
			}
			if (user.getUnionId() != null) {
				mobileRegistedUser.setUnionId(user.getUnionId());
			}
			this.userRepository.save(mobileRegistedUser);
			this.userRepository.delete(user.getId());
			return mobileRegistedUser;
		}

		user.setMobile(mobile);
		user.setPassword(this.passwordEncoder.encode(password));
		this.userRepository.save(user);
		return user;
	}

	@Override
	public User bindUserMobile(String no, String mobile) {
		if (!this.isMobile(mobile)) {
			throw new CustomRuntimeException("400", String.format("邮箱地址[%s]已被其它账户绑定.", mobile));
		}
		User user = userRepository.findOneByNo(no);
		if ((user.getMobile() != null) && user.getMobile().equals(mobile)) {
			return user;
		}

		final User mobileRegistedUser = this.userRepository.findOneByMobile(mobile);
		if (mobileRegistedUser != null) {
			throw new CustomRuntimeException(String.format("手机号码[%s]已被其它帐号绑定，您可以用该手机号登录或更换其它号码", mobile));
		}
		user.setMobile(mobile);
		this.userRepository.save(user);
		return user;
	}

	@Override
	public User bindUserEmail(String no, String email) {
		if (!this.isEmail(email)) {
			throw new CustomRuntimeException("400", String.format("邮箱地址[%s]已被其它账户绑定.", email));
		}
		User user = userRepository.findOneByNo(no);
		if ((user.getEmail() != null) && user.getEmail().equals(email)) {
			return user;
		}
		final User existingUser = this.userRepository.findOneByEmail(email);
		if (existingUser != null) {
			throw new CustomRuntimeException("406", String.format("邮箱格式错误.", email));
		}
		user.setEmail(email);
		return this.userRepository.save(user);
	}

	@Override
	public void changeUserPassword(String no, String password) {
		final User user = this.userRepository.findOneByNo(no);
		user.setPassword(this.passwordEncoder.encode(password));
		this.userRepository.save(user);
	}

	@Override
	public void setUserLoginId(String no, String loginId) {
		final User user = this.userRepository.findOneByNo(no);

		// 登录名称只允许设置一次
		if (StringUtils.hasText(user.getLoginId())) {
			throw new CustomRuntimeException("登录名称已设置, 不允许重新设置");
		}

		final User anotherUser = this.userRepository.findOneByLoginId(loginId);
		if ((anotherUser != null) && (anotherUser.getId() != user.getId())) {
			throw new CustomRuntimeException(String.format("登录名称[%s]已占用", loginId));
		}

		user.setLoginId(loginId);

		this.userRepository.save(user);
	}

	@Override
	public void removeUserByNo(String no) {
		final User user = this.userRepository.findOneByNo(no);

		try {
			this.userRepository.delete(user.getId());
		} catch (final Exception ex) {
			UserServiceImpl.log.warn(ex.getLocalizedMessage());
			throw new CustomRuntimeException(String.format("无法删除指定编号[%s]的用户", no));
		}
	}

	private boolean isMobile(String value) {
		// 手机号正则表达式
		Pattern regex = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = regex.matcher(value);
		return m.matches();
	}

	private boolean isEmail(String value) {
		Pattern regex = Pattern.compile("^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w{2,3}){1,3})$");
		Matcher m = regex.matcher(value);
		return m.matches();
	}

	private User bindOpenId(final User user, final String openId, final WeChatChannel channel) {
		switch (channel) {
		case wechatWebsite:
			user.setWebsiteAppOpenId(openId);
			break;
		case wechatMobileApp:
			user.setMobileAppOpenId(openId);
			break;
		case officialAccount:
			user.setSubscriptionOpenId(openId);
			break;
		}
		return this.userRepository.save(user);
	}

	private User registerByWebsitApp(final String unionId, final String openId) {
		final User registeredUser = this.userRepository.findOneByWebsiteAppOpenId(openId);
		if (registeredUser != null) {// 已经注册过了
			throw new CustomRuntimeException("微信账户已经注册");
		}
		final User user = new User();
		user.setUnionId(unionId);
		user.setWebsiteAppOpenId(openId);
		user.setNo(this.idGeneratorService.generateUserNo());
		return this.userRepository.save(user);
	}

	private User registerByMobileApp(final String unionId, final String openId) {
		final User registeredUser = this.userRepository.findOneByMobileAppOpenId(openId);
		if (registeredUser != null) {// 已经注册过了
			throw new CustomRuntimeException("微信账户已经注册");
		}
		final User user = new User();
		user.setUnionId(unionId);
		user.setMobileAppOpenId(openId);
		user.setNo(this.idGeneratorService.generateUserNo());
		return this.userRepository.save(user);
	}

	private User registerBySubscription(final String unionId, final String openId) {
		final User registeredUser = this.userRepository.findOneBySubscriptionOpenId(openId);
		if (registeredUser != null) {// 已经注册过了
			throw new CustomRuntimeException("微信账户已经注册");
		}
		final User user = new User();
		user.setUnionId(unionId);
		user.setSubscriptionOpenId(openId);
		user.setNo(this.idGeneratorService.generateUserNo());
		return this.userRepository.save(user);
	}
}
