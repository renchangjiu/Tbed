package com.su.dao;

import com.su.pojo.Group;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author su
 * @date 2019/10/20 20:04
 */
@Mapper
public interface GroupMapper {
    List<Group> grouplist();

    Group selectByKey(@Param("id") Long id);

    Integer addgroup(Group group);

    Integer delegroup(@Param("id") Long id);

    Integer setgroup(Group group);
}
