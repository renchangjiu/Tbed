package com.su.service.impl;

import com.su.dao.UserMapper;
import com.su.pojo.*;
import com.su.service.UserService;
import com.su.utils.HashUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
    public Integer getUserTotal() {
        return userMapper.getUserTotal();
    }


}
