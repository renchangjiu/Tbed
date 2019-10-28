package com.su.service.impl;

import com.su.config.SystemConfig;
import com.su.dao.CodeMapper;
import com.su.dao.UserMapper;
import com.su.pojo.*;
import com.su.service.UserService;
import com.su.utils.SendEmail;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author su
 * @date 2019/10/22 14:04
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CodeMapper codeMapper;

    @Autowired
    private SystemConfig systemConfig;

    @Override
    public Result<User> register(UserAddBean userAddBean) {
        Integer countusername = this.countusername(userAddBean.getUsername());
        Integer countmail = this.countmail(userAddBean.getEmail());

        if (countusername != 0 || countmail != 0) {
            return Result.error("注册失败，用户名或邮箱重复，请重试。");
        }
        if (!this.systemConfig.enableRegister) {
            return Result.error("本站已关闭注册功能。");
        }
        User user = new User();
        EmailConfig emailConfig = emailConfigService.getemail();
        String uid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String birthder = df.format(new Date());
        user.setLevel(1);
        user.setUid(uid);
        user.setBirthder(birthder);
        user.setMemory(this.systemConfig.userDefaultMemory);
        user.setGroupid(1L);
        user.setEmail(userAddBean.getEmail());
        user.setUsername(userAddBean.getUsername());
        user.setPassword(new String(Base64.encodeBase64(userAddBean.getPassword().getBytes())));
        Config config = configService.getSourceype();
        // 是否开启邮箱验证
        if (this.systemConfig.enableEmailVerification == 1) {
            user.setStatus(2);
            //初始化邮箱
            MimeMessage message = SendEmail.Emails(emailConfig);
            //注册完发激活链接
            Thread thread = new Thread(() -> {
                SendEmail.sendEmail(message, user.getUsername(), uid, user.getEmail(), emailConfig, config);
            });
            thread.start();
        } else {
            //直接注册
            user.setStatus(1);
        }
        Integer ret = userMapper.register(user);
        if (ret == 1) {
            return Result.success(user);
        } else {
            return Result.error();
        }
    }

    @Override
    public User login(String email, String password) {
        return userMapper.login(email, new String(Base64.encodeBase64(password.getBytes())));
    }

    @Override
    public User getUsers(String email) {
        return userMapper.getUsers(email);
    }


    @Override
    public Integer change(User user) {
        return userMapper.change(user);
    }

    @Override
    public Integer checkUsername(String username) {
        return userMapper.checkUsername(username);
    }

    @Override
    public Integer getUserTotal() {
        return userMapper.getUserTotal();
    }

    @Override
    public Integer deleuser(Long id) {
        return userMapper.deleuser(id);
    }

    @Override
    public Integer countusername(String username) {
        return userMapper.countusername(username);
    }

    @Override
    public Integer countmail(String email) {
        return userMapper.countmail(email);
    }

    @Override
    public List<User> getuserlist(User user) {
        return userMapper.getuserlist(user);
    }

    @Override
    public Integer uiduser(String uid) {
        return userMapper.uiduser(uid);
    }

    @Override
    public User getUsersMail(String uid) {
        return userMapper.getUsersMail(uid);
    }

    @Override
    public Integer setisok(User user) {
        return userMapper.setisok(user);
    }

    @Override
    public Integer setmemory(User user) {
        return userMapper.setmemory(user);
    }

    @Override
    public User getUsersid(Long id) {
        return userMapper.getUsersid(id);
    }

    @Override
    public List<User> getuserlistforgroupid(Long groupid) {
        return userMapper.getuserlistforgroupid(groupid);
    }

    @Transactional
    public Integer usersetmemory(User user, String codestring) {
        Integer ret = userMapper.setmemory(user);
        if (ret > 0) {
            ret = codeMapper.deleteCode(codestring);
        }
        return ret;
    }


}
