package com.su.dao;

import com.su.pojo.EmailConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailConfigMapper {
    EmailConfig getemail();
    Integer updateemail(EmailConfig emailConfig);
}
