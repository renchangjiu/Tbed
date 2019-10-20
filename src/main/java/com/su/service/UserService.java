package com.su.service;

import com.su.pojo.User;

import java.util.List;

public interface UserService {
    //注册
    Integer register(User user);

    //登录
    Integer login(String email, String password);

    //获取用户信息
    User getUsers(String email);

    //修改资料
    Integer change(User user);

    //检查用户名是否重复
    Integer checkUsername(String username);

    Integer getUserTotal();

    List<User> getuserlist(User user);

    Integer deleuser(Long id);

    //查询用户名或者邮箱是否存在
    Integer countusername(String username);

    Integer countmail(String email);

    Integer uiduser(String uid);

    User getUsersMail(String uid);

    Integer setisok(User user);

    Integer setmemory(User user);

    User getUsersid(Long id);

    List<User> getuserlistforgroupid(Long groupid);
}