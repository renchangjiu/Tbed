package com.su.service.impl;

import com.su.dao.GroupMapper;
import com.su.dao.UserMapper;
import com.su.pojo.Group;
import com.su.pojo.Result;
import com.su.pojo.User;
import com.su.service.GroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author su
 * @date 2019/10/20 17:19
 */
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Group> grouplist() {
        return groupMapper.grouplist();
    }

    @Override
    public Group getById(Long id) {
        return this.groupMapper.selectByKey(id);
    }

    @Override
    public Result<Group> getByUserId(Long userId) {
        if (userId == null) {
            // 1 即是默认群组
            Group group = this.groupMapper.selectByKey(1L);
            return Result.success(group);
        } else {
            User user = this.userMapper.getUsersid(userId);
            Group group = this.getById(user.getGroupid());
            return Result.success(group);
        }
    }

    @Override
    public Integer addgroup(Group group) {
        return groupMapper.addgroup(group);
    }

    @Override
    @Transactional
    public Integer delegroup(Long id) {
        Integer ret = 0;
        ret = groupMapper.delegroup(id);
        if (ret > 0) {
            List<User> userList = userMapper.getuserlistforgroupid(id);
            for (User user : userList) {
                User u = new User();
                u.setGroupid(1L);
                u.setUid(user.getUid());
                userMapper.change(u);
            }
        }
        return ret;
    }

    @Override
    public Integer setgroup(Group group) {
        return groupMapper.setgroup(group);
    }
}
