package com.su.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.su.pojo.Key;

import java.util.List;

@Mapper
public interface KeyMapper {
    Key selectByStorageType(@Param("storageType") Integer storageType);

    Key selectByKey(Long id);

    Integer updateKey(Key key);

    List<Key> getKeys();

}
