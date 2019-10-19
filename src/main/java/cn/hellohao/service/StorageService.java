package cn.hellohao.service;

import cn.hellohao.pojo.Result;
import cn.hellohao.pojo.ReturnImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author su
 * @date 2019/10/19 14:26
 */
public interface StorageService {
    /**
     * 使用表单上传
     *
     * @param imageName     图片原始名
     * @param multipartFile multipartFile
     * @param username      用户名
     * @param expireDay     保存时间
     * @return Result<ReturnImage>
     */
    Result<ReturnImage> save(String imageName, MultipartFile multipartFile, String username, Integer expireDay);

    /**
     * 使用图片URL地址上传
     *
     * @param imageName 图片原始名
     * @param imageUrl  URL
     * @param username  用户名
     * @param expireDay 保存时间
     * @return Result<ReturnImage>
     */
    Result<ReturnImage> save(String imageName, String imageUrl, String username, Integer expireDay);

    Result<Boolean> delete(Integer imageId);

    Result<Boolean> batchDelete(Integer[] imageIds);

}
