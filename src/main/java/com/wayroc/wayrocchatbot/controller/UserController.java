package com.wayroc.wayrocchatbot.controller;

import com.wayroc.wayrocchatbot.common.ErrorCode;
import com.wayroc.wayrocchatbot.exception.BusinessException;
import com.wayroc.wayrocchatbot.model.domain.User;
import com.wayroc.wayrocchatbot.model.domain.request.UserLoginRequest;
import com.wayroc.wayrocchatbot.model.domain.request.UserRegisterRequest;
import com.wayroc.wayrocchatbot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器 - 注册 / 登录 / 登出
 */
@RestController
@RequestMapping("/user")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Object register(@RequestBody UserRegisterRequest request) {
        if (request == null) throw new BusinessException(ErrorCode.Null_ERROR, "request is null");
        String userAccount = request.getUserAccount();
        String password = request.getUserPassword();
        String checkPassword = request.getCheckUserPassword();

        if (userAccount == null || password == null || checkPassword == null) {
            throw new BusinessException(ErrorCode.Null_ERROR, "userAccount or password is null");
        }

        long result = userService.userRegister(userAccount, password, checkPassword);
        return result;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Object login(@RequestBody UserLoginRequest request, HttpServletRequest req) {
        if (request == null) throw new BusinessException(ErrorCode.Null_ERROR, "request is null");
        String userAccount = request.getUserAccount();
        String password = request.getUserPassword();
        if (userAccount == null || password == null) {
            throw new BusinessException(ErrorCode.Null_ERROR, "userAccount or password is null");
        }
        User user = userService.userLogin(userAccount, password, req); //有点问题
        return user;
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Object logout(HttpServletRequest request) {
        if (request == null) throw new BusinessException(ErrorCode.Null_ERROR, "request is null");
        int result = userService.userLogout(request);
        return result;
    }
}
