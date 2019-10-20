package com.su.service.impl;

import com.su.dao.ConfigMapper;
import com.su.pojo.Config;
import com.su.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigServiceImpl implements ConfigService {
    @Autowired
    private ConfigMapper configMapper;
    @Override
    public Config getSourceype() {
        return configMapper.getSourceype();
    }

    @Override
    public Integer setSourceype(Config config) {
        return configMapper.setSourceype(config);
    }
}
