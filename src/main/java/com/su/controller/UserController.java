package com.su.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import com.su.pojo.*;
import com.su.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author su
 * @date 2019/10/19 19:23
 */
@Controller
public class UserController extends BaseController {
    @Autowired
    private HttpSession session;

    @Autowired
    private UserService userService;


    @Autowired
    private ConfigService configService;


    @RequestMapping("/login")
    @ResponseBody
    public Result<?> login(String email, String password, Integer logotmp) {
        if ((logotmp - number1) != (istmp1 - number1)) {
            return Result.error("非法登录，请刷新页面重新尝试。");
        }
        Result<User> result = userService.login(email, password);
        if (result.isSuccess()) {
            session.setAttribute("user", result.getData());
        }
        return result;
    }

    @RequestMapping("/logout")
    @ResponseBody
    public Result<?> exit() {
        session.removeAttribute("user");
        session.invalidate();
        return Result.success();
    }


    @PostMapping("/verification")
    @ResponseBody
    public Integer verification(Integer tmp, Integer type) {
        Random random = new Random();
        if (type == 1) {
            number1 = random.nextInt(100);
            istmp1 = tmp;
        }
        if (type == 2) {
            number2 = random.nextInt(100);
            istmp2 = tmp;
        }

        return 1;
    }

    @RequestMapping("/isLogin")
    @ResponseBody
    public Result<?> isLogin2() {
        return Result.success(super.isLogin());
    }

    private Integer number1;
    private Integer istmp1;
    private Integer number2;
    private Integer istmp2;
}
