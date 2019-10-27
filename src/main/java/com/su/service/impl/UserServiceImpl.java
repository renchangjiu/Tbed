package com.su.service.impl;

import com.su.dao.CodeMapper;
import com.su.dao.UserMapper;
import com.su.pojo.Group;
import com.su.pojo.User;
import com.su.service.UserService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    public Integer register(User user) {
        return userMapper.register(user);
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
