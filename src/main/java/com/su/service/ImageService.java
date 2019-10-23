package com.su.service;


import java.util.List;

import com.su.pojo.Image;

import com.su.pojo.Images;
import com.su.pojo.Result;
import com.su.pojo.User;

/**
 * @author su
 * @date 2019/10/19 13:26
 */
public interface ImageService {
    List<Image> listData(Image image);

    Integer delete(Long id);

    Result<Image> insert(Image image);

    Integer countimg(Long userid);

    Images selectByPrimaryKey(Integer id);

    Integer counts(Integer userid);

    Integer setabnormal(String imgname);

    Integer deleimgname(String imgname);

    Integer deleall(Integer id);

    List<Images> gettimeimg(String time);

    /**
     * 获取该用户已使用的总存储空间
     *
     * @return integer
     */
    Integer getUsedMemory(User user);

    /**
     * 根据 imageId 返回其在本地存储中的文件路径
     *
     * @param imageId imageId
     * @return 文件路径
     */
    String getImagePath(long imageId);


}
