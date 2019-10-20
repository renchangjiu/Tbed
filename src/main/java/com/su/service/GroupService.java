package com.su.service;

import com.su.pojo.Group;
import com.su.pojo.Result;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hellohao
 * @version 1.0
 * @date 2019/8/19 16:29
 */
@Service
public interface GroupService {
    List<Group> grouplist();

    Group getById(Long id);

    Result<Group> getByUserId(Long userId);

    Integer addgroup(Group group);

    Integer delegroup(Long id);

    Integer setgroup(Group group);
}
