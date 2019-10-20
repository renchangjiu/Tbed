package com.su.service.impl;

import com.su.dao.GroupMapper;
import com.su.dao.UserMapper;
import com.su.exception.CodeException;
import com.su.pojo.Group;
import com.su.pojo.User;
import com.su.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Hellohao
 * @version 1.0
 * @date 2019/8/19 16:30
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
    public Group idgrouplist(Integer id) {
        return groupMapper.idgrouplist(id);
    }

    @Override
    public Integer addgroup(Group group) {
        return groupMapper.addgroup(group);
    }

    @Override
    @Transactional//默认遇到throw new RuntimeException(“…”);会回滚
    public Integer delegroup(Integer id) {
        Integer ret = 0;
        ret = groupMapper.delegroup(id);
        if(ret>0){
            //userGroupMapper.updateusergroupdefault(id);
            List<User> userList = userMapper.getuserlistforgroupid(id);
            for (User user : userList) {
                User u = new User();
                u.setGroupid(1);
                u.setUid(user.getUid());
                userMapper.change(u);
            }

        }else{
            throw new CodeException("用户之没有设置成功。");
        }
        return ret;
    }

    @Override
    public Integer setgroup(Group group) {
        return groupMapper.setgroup(group);
    }
}
