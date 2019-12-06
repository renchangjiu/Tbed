package com.su.service.impl;

import com.su.config.SystemConfig;
import com.su.pojo.*;
import com.su.service.ImageService;
import com.su.service.KeyService;
import com.su.service.UploadConfigService;
import com.su.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 存储服务处理器
 *
 * @author su
 * @date 2019/10/19 14:31
 */
@Component
public class StorageHandler {

    private final LocalStorageServiceImpl localStorageService;
    private final SystemConfig systemConfig;
    private final ImageService imageService;
    private final UserService userService;
    private final KeyService keyService;

    public StorageHandler(LocalStorageServiceImpl localStorageService, SystemConfig systemConfig, ImageService imageService, UserService userService, KeyService keyService, UploadConfigService uploadConfigService) {
        this.localStorageService = localStorageService;
        this.systemConfig = systemConfig;
        this.imageService = imageService;
        this.userService = userService;
        this.keyService = keyService;
    }

    public Result<Image> saveHand(MultipartFile multipartFile, Integer expireDay, Integer storageType, HttpServletRequest request) {
        Result<Image> result = null;
        if (storageType == 1) {
            // m = nOSImageupload.Imageupload(map, userpath, null, setday);
        } else if (storageType == 2) {
            // m = ossImageupload.ImageuploadOSS(map, userpath, null, setday);
        } else if (storageType == 3) {
            // m = ussImageupload.ImageuploadUSS(map, userpath, null, setday);
        } else if (storageType == 4) {
            // m = kodoImageupload.ImageuploadKODO(map, userpath, null, setday);
        } else if (storageType == 5) {
            result = this.localStorageService.save(multipartFile, expireDay, request);
        } else if (storageType == 6) {
            // m = cosImageupload.ImageuploadCOS(map, userpath, null, setday);
        } else if (storageType == 7) {
            // m = ftpImageupload.ImageuploadFTP(map, userpath, null, setday);
        } else {
            result = Result.error("未获取到对象存储参数，上传失败。");
        }
        return result;
    }

    public Result<Image> saveHand(String imageUrl, Integer expireDay, Integer storageType, HttpServletRequest request) throws Exception {
        Result<Image> result = null;
        if (storageType == 1) {
            // m = nOSImageupload.Imageupload(map, userpath, null, setday);
        } else if (storageType == 2) {
            // m = ossImageupload.ImageuploadOSS(map, userpath, null, setday);
        } else if (storageType == 3) {
            // m = ussImageupload.ImageuploadUSS(map, userpath, null, setday);
        } else if (storageType == 4) {
            // m = kodoImageupload.ImageuploadKODO(map, userpath, null, setday);
        } else if (storageType == 5) {
            result = this.localStorageService.save(imageUrl, expireDay, request);
        } else if (storageType == 6) {
            // m = cosImageupload.ImageuploadCOS(map, userpath, null, setday);
        } else if (storageType == 7) {
            // m = ftpImageupload.ImageuploadFTP(map, userpath, null, setday);
        } else {
            result = Result.error("未获取到对象存储参数，上传失败。");
        }
        return result;
    }


}
