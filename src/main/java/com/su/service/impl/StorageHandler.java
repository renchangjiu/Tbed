package com.su.service.impl;

import com.su.config.SystemConfig;
import com.su.pojo.*;
import com.su.service.ImageService;
import com.su.service.KeyService;
import com.su.service.UploadConfigService;
import com.su.service.UserService;
import com.su.utils.requests.Requests;
import com.su.utils.requests.Response;
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

    /**
     * 判断当前用户是否可以上传该图片
     *
     * @return result
     */
    public Result<Boolean> checkPreservable(MultipartFile multipartFile, User user) {
        if (multipartFile == null) {
            return Result.error();
        }
        return this.checkPreservable((int) (multipartFile.getSize() / 1024), user);
    }

    /**
     * 判断当前用户是否可以上传该图片
     *
     * @return result
     */
    public Result<Boolean> checkPreservable(String imageUrl, User user) {
        try {
            Response response = Requests.get(imageUrl);
            return this.checkPreservable(response.getContent().length / 1024, user);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败，链接异常。");
        }
    }

    /**
     * @param fileSize 文件大小, KB
     */
    private Result<Boolean> checkPreservable(Integer fileSize, User user) {
        if (!this.systemConfig.getTouristUploadable() && user == null) {
            return Result.error("已禁止游客上传,请登陆后使用。");
        }
        // 已使用的总内存, KB
        Integer usedMemory = fileSize + this.imageService.getUsedMemory(user);
        // 可用内存, KB
        Integer totalMemory = 0;
        Integer storageType = this.keyService.getCurrentKey(user).getStorageType();
        if (user == null) {
            totalMemory = this.systemConfig.touristTotalMemory;
        } else {
            totalMemory = userService.getUsers(user.getEmail()).getMemory();
        }
        if (totalMemory != -1 && usedMemory >= totalMemory) {
            return Result.error("上传失败，可用空间不足。");
        }
        Key key = keyService.selectByStorageType(storageType);
        if (key == null) {
            return Result.error("未配置存储源，或存储源配置不正确。");
        }
        Integer uploadMaxSize = user != null ? this.systemConfig.userOnceUploadMaxSize : this.systemConfig.touristOnceUploadMaxSize;
        if (fileSize > uploadMaxSize) {
            return Result.error("超出用户单次上传的限制大小。");
        }
        return Result.success();
    }


}
