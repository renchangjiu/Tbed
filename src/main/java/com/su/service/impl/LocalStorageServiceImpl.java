package com.su.service.impl;

import com.su.config.SystemConfig;
import com.su.pojo.Image;
import com.su.pojo.Result;
import com.su.service.ImageService;
import com.su.service.StorageService;
import com.su.utils.DateUtils;
import com.su.utils.DeleImg;
import com.su.utils.IdWorker;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
    public Result<Image> save(MultipartFile multipartFile, String userPath, Integer expireDay, HttpServletRequest request) {
        long id = IdWorker.singleNextId();
        String imageExt = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String destPath = this.systemConfig.getSavePath() + userPath + File.separator + id + "." + imageExt;
        File dest = new File(destPath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            FileOutputStream out = new FileOutputStream(dest);
            IOUtils.copy(multipartFile.getInputStream(), out);
            out.close();

            Image image = new Image();
            image.setName(multipartFile.getOriginalFilename());
            String url = getDomain(systemConfig, request) + "links/" + userPath + "/" + id + "." + imageExt;
            image.setUrl(url);
            image.setSize(multipartFile.getSize() / 1024);
            if (expireDay > 0) {
                String delImg = DateUtils.plusDay(expireDay);
                DeleImg.charu("/links/" + userPath + "/" + id + "." + imageExt + "|" + delImg + "|" + "5");
            }
            return Result.success(image);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }

    }

    @Override
    public Result<Image> save(String imageUrl, String userPath, Integer expireDay, HttpServletRequest request) {
        //     long id = IdWorker.singleNextId();
        //     String oldfilePath = entry.getValue();
        //     String newfilePath = File.separator + "HellohaoData" + File.separator + username + File.separator + uuid + times + "." + entry.getKey();//
        //     File file = new File(oldfilePath);
        //     File targetFile = new File(newfilePath);
        //     if (!targetFile.getParentFile().exists()) {
        //         targetFile.mkdirs();
        //     }
        //
        //     file.renameTo(new File(newfilePath));//只移动，源目录不存在文件
        //     // 例2：采用数据流模式上传文件（节省内存）,自动创建父级目录
        //     ReturnImage returnImage = new ReturnImage();
        //     returnImage.setImgurl(username + "/" + uuid + times + "." + entry.getKey());
        //     ImgUrl.put(returnImage, ImgUrlUtil.getFileSize2(new File(newfilePath)));
        //     if (setday > 0) {
        //         String deleimg = DateUtils.plusDay(setday);
        //         DeleImg.charu(username + "/" + uuid + times + "." + entry.getKey() + "|" + deleimg + "|" + "5");
        //     }
        // }
        return null;
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
}
