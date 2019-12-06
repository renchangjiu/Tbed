package com.su.service.impl;

import com.su.config.SystemConfig;
import com.su.dao.UserMapper;
import com.su.pojo.*;
import com.su.service.UserService;
import com.su.utils.HashUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
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



    @Override
    public Result<User> login(String email, String password) {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            return Result.error("邮箱或密码为空");
        }

        User user = userMapper.login(email, HashUtils.MD5(password));
        if (user == null) {
            return Result.error("登录失败，你的邮箱或密码不正确，请重试。");
        }
        if (user.getStatus() != 1) {
            return Result.error("您的账号是未激活状态，无法登陆。");
        }
        return Result.success(user);
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



}
