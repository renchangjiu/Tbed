package com.su.dao;

import com.su.pojo.Config;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigMapper {
    Config getSourceype();
    Integer setSourceype(Config config);
}
