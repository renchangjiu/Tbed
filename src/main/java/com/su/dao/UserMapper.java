package com.su.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.su.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper {
    //注册
    Integer register(User user);

    User login(@Param("email") String email, @Param("password") String password);

    //获取用户信息
    User getUsers(@Param("email") String email);

    //修改资料
    Integer change(User user);

    //检查用户名是否重复
    Integer checkUsername(@Param("username") String username);

    Integer getUserTotal();

    List<User> getuserlist(User user);

    //刪除用戶
    Integer deleuser(@Param("id") Long id);

    //查询用户名或者邮箱是否存在
    Integer countusername(@Param("username") String username);

    Integer countmail(@Param("email") String email);

    Integer uiduser(@Param("uid") String uid);

    User getUsersMail(@Param("uid") String uid);

    Integer setisok(User user);

    Integer setmemory(User user);

    User getUsersid(@Param("id") Long id);

    List<User> getuserlistforgroupid(@Param("groupid") Long groupid);

}
