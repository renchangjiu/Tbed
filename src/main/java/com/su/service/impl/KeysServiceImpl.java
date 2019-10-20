package com.su.service.impl;

import com.su.dao.KeysMapper;
import com.su.pojo.Keys;
import com.su.service.KeysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeysServiceImpl implements KeysService {

    @Autowired
    private KeysMapper keysMapper;

    @Override
    public Keys selectByStorageType(Integer storageType) {
        return keysMapper.selectByStorageType(storageType);
    }


    @Override
    public Integer updateKey(Keys key) {
        // TODO Auto-generated method stub
        return keysMapper.updateKey(key);
    }

    @Override
    public List<Keys> getKeys() {
        return null;
    }

}
