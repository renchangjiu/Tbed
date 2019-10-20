package com.su.service;

import com.su.pojo.UploadConfig;
import org.springframework.stereotype.Service;

@Service
public interface UploadConfigService {
    UploadConfig getUpdateConfig();
    Integer setUpdateConfig(UploadConfig updateConfig);
}
