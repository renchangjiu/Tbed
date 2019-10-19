package cn.hellohao.service;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.hellohao.pojo.Images;

/**
 * @author su
 * @date 2019/10/19 13:26
 */
public interface ImageService {
    List<Images> selectimg(Images images);

    Integer deleimg(Integer id);

    Integer countimg(Integer userid);

    Images selectByPrimaryKey(Integer id);

    Integer counts(Integer userid);

    Integer setabnormal(String imgname);

    Integer deleimgname(String imgname);

    Integer deleall(Integer id);

    List<Images> gettimeimg(String time);

    Integer getusermemory(Integer userid);

    /**
     * 根据 imageId 返回其在本地存储中的文件路径
     *
     * @param imageId imageId
     * @return 文件路径
     */
    String getImagePath(long imageId);

    /**
     * 返回本地存储中的保存图片的路径
     *
     * @return 图片的保存路径
     */
    String getSavePath();
}
