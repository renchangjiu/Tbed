package com.su.service;

import com.su.pojo.EmailConfig;
import org.springframework.stereotype.Service;

@Service
public interface EmailConfigService {
    EmailConfig getemail();
    Integer updateemail(EmailConfig emailConfig);
}
