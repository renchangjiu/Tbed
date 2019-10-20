package com.su.service.impl;

import com.su.dao.EmailConfigMapper;
import com.su.pojo.EmailConfig;
import com.su.service.EmailConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailConfigService {
    @Autowired
    EmailConfigMapper emailConfigMapper;
    @Override
    public EmailConfig getemail() {
        return emailConfigMapper.getemail();
    }

    @Override
    public Integer updateemail(EmailConfig emailConfig) {
        return emailConfigMapper.updateemail(emailConfig);
    }
}
