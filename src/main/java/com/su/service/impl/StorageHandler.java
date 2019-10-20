package com.su.service.impl;

import com.su.config.SystemConfig;
import com.su.pojo.*;
import com.su.service.ImageService;
import com.su.service.KeysService;
import com.su.service.UploadConfigService;
import com.su.service.UserService;
import com.su.utils.GetCurrentSource;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final KeysService keysService;

    public StorageHandler(LocalStorageServiceImpl localStorageService, SystemConfig systemConfig, ImageService imageService, UserService userService, KeysService keysService, UploadConfigService uploadConfigService) {
        this.localStorageService = localStorageService;
        this.systemConfig = systemConfig;
        this.imageService = imageService;
        this.userService = userService;
        this.keysService = keysService;
    }

    public Result<Image> saveHand(MultipartFile multipartFile, String username, Integer expireDay, Integer storageType, HttpServletRequest request) {
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
            result = this.localStorageService.save(multipartFile, username, expireDay, request);
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
     * 判断当前用户是否可以上传图片
     *
     * @return result
     */
    public Result<Boolean> canSave(User user) {
        if (!this.systemConfig.touristUploadable && user == null) {
            return Result.error("已禁止游客上传,请登陆后使用。");
        }
        // 已使用的总内存, KB
        Integer usedMemory = 0;
        // 可用内存, KB
        Integer totalMemory = 0;
        Integer storageType = 0;
        if (user == null) {
            storageType = GetCurrentSource.GetSource(null);
            totalMemory = this.systemConfig.touristTotalMemory;
            usedMemory = imageService.getUsedMemory(0);
        } else {
            storageType = GetCurrentSource.GetSource(user.getId());
            totalMemory = userService.getUsers(user.getEmail()).getMemory();
            usedMemory = imageService.getUsedMemory(user.getId());
        }
        Keys key = keysService.selectByStorageType(storageType);
        if (key == null) {
            return Result.error("未配置存储源，或存储源配置不正确。");
        }

        if (totalMemory != -1 && usedMemory >= totalMemory) {
            return Result.error("上传失败，可用空间不足。");
        }
        return Result.success();
    }
}
