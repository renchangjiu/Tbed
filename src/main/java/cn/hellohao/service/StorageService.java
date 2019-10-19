package cn.hellohao.service;

import cn.hellohao.pojo.Image;
import cn.hellohao.pojo.Result;
import cn.hellohao.pojo.ReturnImage;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author su
 * @date 2019/10/19 14:26
 */
public interface StorageService {
    /**
     * 使用表单上传
     *
     * @param multipartFile multipartFile
     * @param username      用户名
     * @param expireDay     保存时间
     * @param request       request
     * @return Result<ReturnImage>
     */
    Result<Image> save(MultipartFile multipartFile, String userPath, Integer expireDay, HttpServletRequest request);

    /**
     * 使用图片URL地址上传
     *
     * @param imageUrl  URL
     * @param username  用户名
     * @param expireDay 保存时间
     * @param request   request
     * @return Result<ReturnImage>
     */
    Result<Image> save(String imageUrl, String userPath, Integer expireDay, HttpServletRequest request);

    Result<Boolean> delete(Integer imageId);

    Result<Boolean> batchDelete(Integer[] imageIds);

    default String getDomain(String enableHttps, HttpServletRequest request) {
        String protocol = "1".equals(enableHttps) ? "https" : "http";
        String localAddr = request.getLocalAddr();
        localAddr = "0:0:0:0:0:0:0:1".equals(localAddr) ? "localhost" : localAddr;
        return protocol + "://" + localAddr + ":" + request.getLocalPort() + "/";
    }

}
