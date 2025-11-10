package com.wayroc.wayrocchatbot.controller;

import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器 - 注册 / 登录 / 登出
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Object register(@RequestParam String userAccount,
                           @RequestParam String password,
                           @RequestParam String Checkpassword,
                           @RequestParam(required = false) String email,
                           @RequestParam(required = false) String phone) {
        // TODO: 实现注册逻辑
        return null;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Object login(@RequestParam String userAccount,
                        @RequestParam String password) {
        // TODO: 实现登录逻辑
        return null;
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Object logout() {
        // TODO: 实现登出逻辑
        return null;
    }
}
