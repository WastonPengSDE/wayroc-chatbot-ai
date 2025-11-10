package com.wayroc.wayrocchatbot.service.impl;

import com.wayroc.wayrocchatbot.model.domain.User;
import com.wayroc.wayrocchatbot.repositoty.UserRepository;
import com.wayroc.wayrocchatbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkUserPassword) {

        // 1. 参数校验
        if (userAccount == null || userPassword == null || checkUserPassword == null) {
            return 0;
        }
        if (userAccount.length() < 4) {
            return 0;
        }
        if (userPassword.length() < 8) {
            return 0;
        }
        if (!userPassword.equals(checkUserPassword)) {
            return 0;
        }

        // 2. 账户是否重复
        if (userRepository.existsByUserAccount(userAccount)) {
            return 0;
        }

        // 3. 加密密码（MD5）
        String encryptPassword = DigestUtils.md5DigestAsHex(userPassword.getBytes());

        // 4. 保存用户
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserRole("user");

        User savedUser = userRepository.save(user);
        return savedUser.getId(); // 返回新用户ID
    }

    @Override
    public User userLogin(String userAccount, String userPassword) {
        if (userAccount == null || userPassword == null) {
            throw new IllegalArgumentException("userAccount or userPassword is null");
        }

        // 1. 加密输入的密码
        String encryptPassword = DigestUtils.md5DigestAsHex(userPassword.getBytes());

        // 2. 从数据库中查找匹配用户
        Optional<User> userOpt = userRepository.findByUserAccountAndUserPassword(userAccount, encryptPassword);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("userAccount or userPassword is incorrect");
        }

        // 3. 登录成功，返回用户
        return userOpt.get();
    }

    @Override
    public int userLogout(String userAccount) {
        // 模拟登出逻辑
        // 实际项目中可以清除 session、token 或缓存信息
        if (userAccount == null || userAccount.isEmpty()) {
            return 0; // 参数无效
        }
        // 这里暂时返回 1 表示登出成功
        System.out.println("User " + userAccount + " logged out successfully.");
        return 1;
    }
}
