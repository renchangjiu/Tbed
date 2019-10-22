package com.su.service;


import com.su.pojo.Key;
import com.su.pojo.User;

import java.util.List;

public interface KeyService {
    Key selectByStorageType(Integer storageType);

    Key getCurrentKey(User user);

    //修改key
    Integer updateKey(Key key);
    List<Key> getKeys();

    Key getById(Long id);
}
