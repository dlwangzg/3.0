package com.leadingsoft.bizfuse.base.uap.service;

import com.leadingsoft.bizfuse.base.uap.dto.WeChatRegisterDTO.WeChatChannel;
import com.leadingsoft.bizfuse.base.uap.model.User;

public interface UserService {

	////////////////////////////////////////////////////////////////////////////
	//// 查询类接口
	////////////////////////////////////////////////////////////////////////////
    /**
     * 通过身份标识查找用户
     *
     * @param identity 身份标识，可能是 LoginId/Mobile/Email
     * @return 成功时返回找到的用户对象; 失败时返回 null
     */
    User findUserByIdentity(final String identity);

    ///////////////////////////////////////////////////////////////////////////////
    //// 注册类接口
    ///////////////////////////////////////////////////////////////////////////////
    /**
     * 创建用户帐号
     *
     * @param user 新用户的参数信息
     * @return 创建后的用户对象
     */
    User createUser(final User user);
    
    /**
     * 通过用户名密码注册新账户
     *
     * @param username 登录ID（loginId）
     * @param password 登录密码
     * @param name 真实姓名
     * @param nickname 用户别名
     * @return 注册成功后的用户对象
     */
    User registerUserByUsernamePassword(String username, String password, String name, String nickname);

    /**
     * 从微信渠道注册新账户
     *
     * @param openId 微信OpenId
     * @param unionId 微信UnionId
     * @param channel 微信渠道（公众号、网站应用、手机应用）
     * @return 注册成功后的用户对象
     */
    User registerUserByWeChat(String openId, String unionId, WeChatChannel channel);

    /**
     * 通过邮件注册新账户
     *
     * @param email 邮件地址
     * @param password 登录密码
     * @param name 真实姓名
     * @param nickname 用户别名
     * @return 注册成功后的用户对象
     */
    User registerUserByEmail(String email, String password, String name, String nickname);

    /**
     * 通过手机号注册新账户
     *
     * @param mobile 手机号
     * @param password 登录密码
     * @param name 真实姓名
     * @param nickname 用户别名
     * @return 注册成功后的用户对象
     */
    User registerUserByMobile(String mobile, String password, String name, String nickname);

    
    ///////////////////////////////////////////////////////////////////////////////
    //// 更新类接口
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * 绑定手机号和密码（微信注册的账户，绑定手机号和密码）
     *
     * @param no
     * @param mobile
     * @param password
     * @return
     */
    User bindUserMobileAndPassword(String no, String mobile, String password);

    /**
     * 用户绑定手机号
     *
     * @param user
     * @param mobile
     * @return
     */
    User bindUserMobile(String no, String mobile);

    /**
     * 用户绑定邮箱
     *
     * @param user
     * @param email
     * @return
     */
    User bindUserEmail(String no, String email);

    /**
     * 修改手机用户的登录密码
     *
     * @param no 用户No
     * @param password 登录密码，不允许为空
     */
    void changeUserPassword(String no, String password);
    
    /**
     * 设置用户的登录ID
     *
     * @param no 用户编号
     * @param loginId 登录ID
     * @return 修改后的用户对象
     */
    void setUserLoginId(String no, String loginId);

    /**
     * 删除用户帐号
     *
     * @param no 用户编号
     */
    void removeUserByNo(String no);

}
