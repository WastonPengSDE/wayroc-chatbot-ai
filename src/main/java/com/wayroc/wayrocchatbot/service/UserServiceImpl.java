package com.wayroc.wayrocchatbot.service;

import com.wayroc.wayrocchatbot.common.ErrorCode;
import com.wayroc.wayrocchatbot.exception.BusinessException;
import com.wayroc.wayrocchatbot.model.domain.User;
import com.wayroc.wayrocchatbot.repositoty.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.usertype.BaseUserTypeSupport;
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
            throw new BusinessException(ErrorCode.Null_ERROR,"the parameter is null check you register info");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "the length of user account is less than 4");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "the length of user password is less than 8");
        }
        if (!userPassword.equals(checkUserPassword)) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "the check user password does not match");
        }

        // 2. 账户是否重复
        if (userRepository.existsByUserAccount(userAccount)) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "the user account already exists");
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
    public User userLogin(String userAccount, String userPassword , HttpServletRequest request) {
        if (userAccount == null || userPassword == null) {
            throw new BusinessException(ErrorCode.Null_ERROR,"the parameter is null check you login info");
        }

        // 1. 加密输入的密码
        String encryptPassword = DigestUtils.md5DigestAsHex(userPassword.getBytes());

        // 2. 从数据库中查找匹配用户
        Optional<User> userOpt = userRepository.findByUserAccountAndUserPassword(userAccount, encryptPassword);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ErrorCode.PARMAS_ERROR, "the check user password does not exist");
        }

        User user = userOpt.get();

        // 3. 登录成功，返回用户

        User cleanedUser = new User();
        cleanedUser.setId(user.getId());
        cleanedUser.setUserAccount(user.getUserAccount());
        cleanedUser.setUserRole(user.getUserRole());
        cleanedUser.setGender(user.getGender());
        cleanedUser.setPhone(user.getPhone());
        cleanedUser.setCreateTime(user.getCreateTime());
        cleanedUser.setUpdateTime(user.getUpdateTime());

        request.getSession().setAttribute("userLoginState", user);

        return cleanedUser;
    }

    @Override
    public int userLogout(HttpServletRequest request ) {
        request.getSession().removeAttribute("userLoginState");
        return 1;

    }
}
