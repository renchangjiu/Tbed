package com.su.service;

import com.su.config.SystemConfig;
import com.su.pojo.Image;
import com.su.pojo.Result;
import org.apache.commons.lang3.StringUtils;
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
     * @param expireDay     保存时间
     * @param request       request
     * @return Result<ReturnImage>
     */
    Result<Image> save(MultipartFile multipartFile, String userPath, Integer expireDay, HttpServletRequest request);

    /**
     * 使用图片URL地址上传
     *
     * @param imageUrl  URL
     * @param expireDay 保存时间
     * @param request   request
     * @return Result<ReturnImage>
     */
    Result<Image> save(String imageUrl, String userPath, Integer expireDay, HttpServletRequest request);

    Result<Boolean> delete(Integer imageId);

    Result<Boolean> batchDelete(Integer[] imageIds);

    default String getDomain(SystemConfig config, HttpServletRequest request) {
        if (StringUtils.isNotEmpty(config.imageSaveDomain)) {
            return config.imageSaveDomain;
        }
        String protocol = "http";
        String localAddr = request.getLocalAddr();
        localAddr = "0:0:0:0:0:0:0:1".equals(localAddr) ? "localhost" : localAddr;
        return protocol + "://" + localAddr + ":" + request.getLocalPort() + "/";
    }

}
