package com.su.service.impl;

import com.su.config.SystemConfig;
import com.su.pojo.Image;
import com.su.pojo.Images;
import com.su.pojo.Result;
import com.su.pojo.ReturnImage;
import com.su.service.ImageService;
import com.su.service.StorageService;
import com.su.utils.*;
import com.su.utils.requests.Requests;
import com.su.utils.requests.Response;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * 本地存储
 *
 * @author su
 * @date 2019/10/19 14:31
 */
@Service
public class LocalStorageServiceImpl implements StorageService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ImageService imageService;

    @Autowired
    private SystemConfig systemConfig;

    @Override
    public Result<Image> save(MultipartFile multipartFile, Integer expireDay, HttpServletRequest request) {
        try {
            long id = IdWorker.singleNextId();
            String imageExt = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            String destPath = this.genLocalSavePath(id, imageExt);
            String url = this.genLocalUrl(id, imageExt, request);
            File dest = new File(destPath);

            FileOutputStream out = new FileOutputStream(dest);
            IOUtils.copy(multipartFile.getInputStream(), out);
            out.close();

            Image image = new Image();
            image.setName(multipartFile.getOriginalFilename());

            image.setUrl(url);
            image.setSize(multipartFile.getSize() / 1024);
            if (expireDay > 0) {
                String delImg = DateUtils.plusDay(expireDay);
                String urlPart1 = "links/";
                String urlPart2 = new SimpleDateFormat("yyyyMMdd/").format(new Date());
                DeleImg.charu(urlPart1 + urlPart2 + id + "." + imageExt + "|" + delImg + "|" + "5");
            }
            return Result.success(image);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }

    }

    @Override
    public Result<Image> save(String imageUrl, Integer expireDay, HttpServletRequest request) throws Exception {
        long id = IdWorker.singleNextId();
        Response response = Requests.get(imageUrl);
        byte[] content = response.getContent();
        if (content.length <= 0) {
            return Result.error("链接错误。");
        }
        //判断文件头是否是图片
        String imageExt = ImageType.checkImageType(Arrays.copyOf(content, 2));
        if (org.apache.commons.lang3.StringUtils.isEmpty(imageExt)) {
            return Result.error("不是图片。");
        }
        String localUrl = this.genLocalUrl(id, imageExt, request);
        // 保存到本地
        String destPath = this.genLocalSavePath(id, imageExt);
        FileOutputStream out = new FileOutputStream(destPath);
        IOUtils.write(content, out);
        out.close();

        Image image = new Image();
        image.setUrl(localUrl);
        image.setSize((long) (content.length / 1024));
        // 超期处理
        if (expireDay > 0) {
            String delImg = DateUtils.plusDay(expireDay);
            String urlPart1 = "links/";
            String urlPart2 = new SimpleDateFormat("yyyyMMdd/").format(new Date());
            DeleImg.charu(urlPart1 + urlPart2 + id + "." + imageExt + "|" + delImg + "|" + "5");
        }
        return Result.success(image);
    }

    @Override
    public Result<Boolean> delete(Integer imageId) {
        try {
            String filePath = this.imageService.getImagePath(imageId);
            File file = new File(filePath);
            if (file.delete()) {
                return Result.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error();
    }

    @Override
    public Result<Boolean> batchDelete(Integer[] imageIds) {
        return null;
    }

    /**
     * 获取本地网络访问路径
     */
    private String genLocalUrl(Long id, String imageExt, HttpServletRequest request) {
        String date = new SimpleDateFormat("yyyyMMdd/").format(new Date());
        return getDomain(systemConfig, request) + "links/" + date + id + "." + imageExt;
    }

    /**
     * 获取本地磁盘保存路径
     */
    private String genLocalSavePath(Long id, String imageExt) {
        String date = new SimpleDateFormat("yyyyMMdd/").format(new Date());
        String destPath = this.systemConfig.getSavePath() + date + id + "." + imageExt;
        File dest = new File(destPath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        return destPath;
    }
}
