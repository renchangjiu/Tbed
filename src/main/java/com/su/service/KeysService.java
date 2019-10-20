package com.su.service;


import com.su.pojo.Keys;

import java.util.List;

public interface KeysService {
    Keys selectByStorageType(Integer storageType);

    //修改key
    Integer updateKey(Keys key);
    List<Keys> getKeys();
}
