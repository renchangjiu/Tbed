package com.su.service.impl;

import com.su.dao.UploadConfigMapper;
import com.su.pojo.UploadConfig;
import com.su.service.UploadConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadConfigServiceImpl implements UploadConfigService {
    @Autowired
    private UploadConfigMapper uploadConfigMapper;

    @Override
    public UploadConfig getUpdateConfig() {
        return uploadConfigMapper.getUpdateConfig();
    }

    @Override
    public Integer setUpdateConfig(UploadConfig uploadConfig) {
        return uploadConfigMapper.setUpdateConfig(uploadConfig);
    }
}

