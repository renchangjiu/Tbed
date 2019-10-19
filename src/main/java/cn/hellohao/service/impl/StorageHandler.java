package cn.hellohao.service.impl;

import cn.hellohao.pojo.Image;
import cn.hellohao.pojo.Result;
import cn.hellohao.pojo.ReturnImage;
import cn.hellohao.utils.LocUpdateImg;
import com.sun.org.apache.regexp.internal.RE;
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

    @Autowired
    private LocalStorageServiceImpl localStorageService;

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

}
