package com.su.controller;

import com.su.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;

/**
 * @author su
 * @date 2019/10/21 10:23
 */
public class BaseController {
    @Autowired
    private HttpSession session;

    /**
     * 获取当前登录用户
     */
    User getCurrentLoginUser() {
        return (User) this.session.getAttribute("user");
    }

    /**
     * 判断当前是否登录
     */
    boolean isLogin() {
        return this.session.getAttribute("user") != null;
    }
}
