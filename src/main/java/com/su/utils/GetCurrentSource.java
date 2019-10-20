package com.su.utils;

import com.su.pojo.Group;
import com.su.pojo.User;
import com.su.service.impl.GroupServiceImpl;
import com.su.service.impl.UserGroupServiceImpl;
import com.su.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author su
 * @date 2019/10/20 17:19
 */
@Component
public class GetCurrentSource {
    @Autowired
    private GroupServiceImpl groupServiceImpl;
    @Autowired
    private UserServiceImpl userServiceImpl;

    private static GroupServiceImpl groupService;
    private static UserServiceImpl userService;

    @PostConstruct
    public void init() {
        groupService = groupServiceImpl;
        userService = userServiceImpl;
    }


    public static Integer GetSource(Long userId) {
        Integer ret = 0;
        if (userId == null) {
            // 1 即是默认群组
            Group group = groupService.getById(1L);
            ret = group.getKeyid();
        } else {
            User user = userService.getUsersid(userId);
            Group group = groupService.getById(user.getGroupid());
            ret = group.getKeyid();
        }
        return ret;
    }
}
