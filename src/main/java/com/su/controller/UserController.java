package com.su.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.su.config.SystemConfig;
import com.su.pojo.*;
import com.su.service.*;
import com.su.utils.SendEmail;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private SystemConfig systemConfig;

    @Autowired
    private EmailConfigService emailConfigService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private UploadConfigService uploadConfigService;

    @Autowired
    private SysConfigService sysConfigService;

    @RequestMapping("/register")
    @ResponseBody
    public Result<?> register(@Valid UserAddBean userAddBean, Integer zctmp, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error(bindingResult.getFieldError().getDefaultMessage());
        }
        if ((zctmp - number2) != (istmp2 - number2)) {
            return Result.error("非法注册，请刷新页面后重新尝试。");
        }
        return this.userService.register(userAddBean);
    }


    @RequestMapping("/login")
    @ResponseBody
    public Result<?> login(String email, String password, Integer logotmp) {
        if ((logotmp - number1) != (istmp1 - number1)) {
            return Result.error("非法登录，请刷新页面重新尝试。");
        }
        User user = userService.login(email, password);
        if (user == null) {
            return Result.error("登录失败，你的邮箱或密码不正确，请重试。");
        }
        if (user.getStatus() != 1) {
            return Result.error("您的账号是未激活状态，无法登陆。");
        }
        session.setAttribute("user", user);
        return Result.success();
    }

    @RequestMapping("/logout")
    @ResponseBody
    public Result<?> exit() {
        session.removeAttribute("user");
        session.invalidate();
        return Result.success();
    }


    /**
     * 邮箱激活
     */
    @RequestMapping("/activation")
    public String activation(Model model, String activation, String username) {
        Config config = configService.getSourceype();
        Integer ret = 0;
        User user = userService.getUsersMail(activation);
        model.addAttribute("config", config);
        if (user != null && user.getStatus() == 0) {
            model.addAttribute("setisok", ret);
            model.addAttribute("username", username);
            return "isok";
        } else {
            return "redirect:/index";
        }

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
