package com.su.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.su.pojo.*;
import com.su.service.*;
import com.su.utils.SendEmail;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

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
    private EmailConfigService emailConfigService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private UploadConfigService uploadConfigService;

    @Autowired
    private SysConfigService sysConfigService;

    @RequestMapping("/register")
    @ResponseBody
    public String Register(@Valid User u, Integer zctmp) {
        JSONObject jsonObject = new JSONObject();
        if ((zctmp - number2) == (istmp2 - number2)) {
            //取当前时间
            User user = new User();
            UploadConfig updateConfig = uploadConfigService.getUpdateConfig();
            EmailConfig emailConfig = emailConfigService.getemail();
            Integer countusername = userService.countusername(u.getUsername());
            Integer countmail = userService.countmail(u.getEmail());
            SysConfig sysConfig = sysConfigService.getstate();
            if (sysConfig.getRegister() == 1) {
                if (countusername == 0 && countmail == 0) {
                    String uid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
                    String birthder = df.format(new Date());// new Date()为获取当前系统时间
                    user.setLevel(1);
                    user.setUid(uid);
                    user.setBirthder(birthder);
                    user.setMemory(updateConfig.getUsermemory());
                    user.setGroupid(1L);
                    user.setEmail(u.getEmail());
                    user.setUsername(u.getUsername());
                    user.setPassword(new String(Base64.encodeBase64(user.getPassword().getBytes())));
                    //查询是否启用了邮箱验证。
                    Config config = configService.getSourceype();
                    System.err.println("是否启用了邮箱激活：" + emailConfig.getUsing());
                    Integer type = 0;
                    if (emailConfig.getUsing() == 1) {
                        user.setIsok(0);
                        //初始化邮箱
                        MimeMessage message = SendEmail.Emails(emailConfig);
                        //注册完发激活链接
                        Thread thread = new Thread(() -> {
                            Integer a = SendEmail.sendEmail(message, user.getUsername(), uid, user.getEmail(), emailConfig, config);
                        });
                        thread.start();
                        type = 1;
                    } else {
                        //直接注册
                        user.setIsok(1);
                        type = 2;
                    }
                    Integer ret = userService.register(user);
                    if (ret > 0) {
//                    UserGroup userGroup = new UserGroup();
//                    User user1 = userService.getUsersMail(uid);
//                    userGroup.setUserid(user1.getId());
//                    userGroup.setGroupid(1);
//                    userGroupService.addusergroup(userGroup);
                    }
                    jsonObject.put("ret", ret);
                    jsonObject.put("zctype", type);
                } else {
                    jsonObject.put("ret", -2);
                }
            } else {
                jsonObject.put("ret", -3); //管理员关闭的注册
            }
        } else {
            jsonObject.put("ret", -4);//非法注册
        }
        return jsonObject.toString();
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
        if (user.getIsok() != 1) {
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
        if (user != null && user.getIsok() == 0) {
            Integer setisok = userService.uiduser(activation);
            model.addAttribute("setisok", ret);
            model.addAttribute("username", username);

            return "isok";
        } else {
            return "redirect:/index";
        }

    }

    @PostMapping(value = "/verification")
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
