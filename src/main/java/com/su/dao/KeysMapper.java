package com.su.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.su.pojo.Keys;

import java.util.List;

@Mapper
public interface KeysMapper {
    Keys selectByStorageType(@Param("storageType") Integer storageType);

    //修改key
    Integer updateKey(Keys key);

    List<Keys> getKeys();

}
