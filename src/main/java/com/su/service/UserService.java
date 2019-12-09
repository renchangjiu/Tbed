package com.su.service;

import com.su.pojo.Result;
import com.su.pojo.User;
import com.su.pojo.UserAddBean;

import java.util.List;

public interface UserService {

    Result<User> login(String email, String password);

    //获取用户信息
    User getUsers(String email);

    Integer getUserTotal();

}
