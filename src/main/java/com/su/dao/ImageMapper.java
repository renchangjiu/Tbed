package com.su.dao;

import java.util.List;

import com.su.pojo.Image;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.su.pojo.Images;

/**
 * @author su
 * @date 2019/10/20 11:09
 */
@Mapper
public interface ImageMapper {

    Integer insert(Image image);

    List<Images> selectimg(Images images);

    Integer countimg(@Param("userid") Long userid);

    Integer delete(@Param("id") Long id);

    Images selectByPrimaryKey(@Param("id") Integer id);

    Integer counts(@Param("userid") Integer userid);

    Integer setabnormal(@Param("imgname") String imgname);

    Integer deleimgname(@Param("imgname") String imgname);

    Integer deleall(@Param("id") Integer id);

    List<Images> gettimeimg(@Param("time") String time);

    Integer getusermemory(@Param("userid") Long userid);
}
