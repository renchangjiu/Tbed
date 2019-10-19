package cn.hellohao.service.impl;

import cn.hellohao.pojo.Result;
import cn.hellohao.pojo.ReturnImage;
import cn.hellohao.service.ImageService;
import cn.hellohao.service.StorageService;
import cn.hellohao.utils.DateUtils;
import cn.hellohao.utils.DeleImg;
import cn.hellohao.utils.IdWorker;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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


    @Override
    public Result<ReturnImage> save(String imageName, MultipartFile multipartFile, String username, Integer expireDay) {
        long id = IdWorker.singleNextId();
        String imageExt = FilenameUtils.getExtension(imageName);
        String destPath = this.imageService.getSavePath() + username + File.separator + id + "." + imageExt;
        File dest = new File(destPath);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            FileOutputStream out = new FileOutputStream(dest);
            IOUtils.copy(multipartFile.getInputStream(), out);
            out.close();

            ReturnImage returnImage = new ReturnImage();
            returnImage.setName(multipartFile.getOriginalFilename());
            returnImage.setUrl(username + "/" + id + "." + imageExt);
            returnImage.setSize(multipartFile.getSize());
            if (expireDay > 0) {
                String delImg = DateUtils.plusDay(expireDay);
                DeleImg.charu(username + "/" + id + "." + imageExt + "|" + delImg + "|" + "5");
            }
            return Result.success(returnImage);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            return Result.error(e.getMessage());
        }

    }

    @Override
    public Result<ReturnImage> save(String imageName, String imageUrl, String username, Integer expireDay) {
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
