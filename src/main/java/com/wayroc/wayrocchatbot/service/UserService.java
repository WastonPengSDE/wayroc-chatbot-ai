package com.wayroc.wayrocchatbot.service;

import com.wayroc.wayrocchatbot.model.domain.User;

/**
 * 用户服务接口
 * 定义用户注册、登录、登出等核心业务逻辑
 *
 * @author Wayroc
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkUserPassword 校验密码
     * @return 新用户的ID（注册失败返回0）
     */
    long userRegister(String userAccount, String userPassword, String checkUserPassword);

    /**
     * 用户登录
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @return 登录成功返回用户对象
     */
    User userLogin(String userAccount, String userPassword);

    /**
     * 用户登出
     *
     * @param userAccount 用户账号
     * @return 1 表示登出成功，0 表示失败
     */
    int userLogout(String userAccount);
}
