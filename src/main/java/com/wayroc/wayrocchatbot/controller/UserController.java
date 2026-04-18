package com.wayroc.wayrocchatbot.controller;

import com.wayroc.wayrocchatbot.authentication.JwtUtils;
import com.wayroc.wayrocchatbot.common.BaseResponse;
import com.wayroc.wayrocchatbot.common.ErrorCode;
import com.wayroc.wayrocchatbot.common.ResultUtils;
import com.wayroc.wayrocchatbot.exception.BusinessException;
import com.wayroc.wayrocchatbot.model.domain.User;
import com.wayroc.wayrocchatbot.model.domain.request.UserLoginRequest;
import com.wayroc.wayrocchatbot.model.domain.request.UserRegisterRequest;
import com.wayroc.wayrocchatbot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器 - 注册 / 登录 / 登出
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest request) {
        if (request == null) throw new BusinessException(ErrorCode.Null_ERROR, "request is null");
        String userAccount = request.getUserAccount();
        String password = request.getUserPassword();
        String checkPassword = request.getCheckUserPassword();

        if (userAccount == null || password == null || checkPassword == null) {
            throw new BusinessException(ErrorCode.Null_ERROR, "userAccount or password is null");
        }

        long result = userService.userRegister(userAccount, password, checkPassword);
        return ResultUtils.success(result); // todo
    }

    /**
     * 用户登录，返回 JWT
     */
    @PostMapping("/login")
    public BaseResponse<Map<String, Object>> login(@RequestBody UserLoginRequest request, HttpServletRequest req) {
        if (request == null) throw new BusinessException(ErrorCode.Null_ERROR, "request is null");
        String userAccount = request.getUserAccount();
        String password = request.getUserPassword();
        if (userAccount == null || password == null) {
            throw new BusinessException(ErrorCode.Null_ERROR, "userAccount or password is null");
        }
        User user = userService.userLogin(userAccount, password, req);
        String token = jwtUtils.generateToken(user.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return ResultUtils.success(data);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Object logout(HttpServletRequest request) {
        if (request == null) throw new BusinessException(ErrorCode.Null_ERROR, "request is null");
        int result = userService.userLogout(request);
        return result; //todo
    }// todo
}