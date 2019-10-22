package com.su.service.impl;

import com.su.dao.GroupMapper;
import com.su.dao.KeyMapper;
import com.su.pojo.Group;
import com.su.pojo.Key;
import com.su.pojo.User;
import com.su.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author su
 * @date 2019/10/22 14:11
 */
@Service
public class KeyServiceImpl implements KeyService {

    @Autowired
    private KeyMapper keyMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Override
    public Key selectByStorageType(Integer storageType) {
        return keyMapper.selectByStorageType(storageType);
    }

    @Override
    public Key getCurrentKey(User user) {
        long groupId = user != null ? user.getGroupid() : Group.DEFAULT_ID;
        Group group = this.groupMapper.selectByKey(groupId);
        return this.getById(group.getKeyId());
    }


    @Override
    public Integer updateKey(Key key) {
        return keyMapper.updateKey(key);
    }

    @Override
    public List<Key> getKeys() {
        return null;
    }

    @Override
    public Key getById(Long id) {
        return this.keyMapper.selectByKey(id);
    }

}
