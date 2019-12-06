package com.su.controller;

import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.su.config.SystemConfig;
import com.su.pojo.*;
import com.su.service.*;
import com.su.service.impl.*;
import com.su.utils.*;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author su
 * @date 2019/10/19 12:07
 */
@Controller
public class ImageController extends BaseController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private KeyService keyService;
    @Autowired
    private ImageService imageService;

    @Autowired
    private StorageHandler storageHandler;


    @RequestMapping("/up")
    @ResponseBody
    public Result<?> upload(MultipartFile file, Integer expireDay) {
        if (!super.isLogin()) {
            return Result.error("请先登录");
        }
        User user = super.getCurrentLoginUser();
        // Result<Boolean> booleanResult = this.storageHandler.checkPreservable(file, user);
        // if (booleanResult.isNotSuccess()) {
        //     return booleanResult;
        // }
        Key key = this.keyService.getCurrentKey(user);
        Result<Image> result = this.storageHandler.saveHand(file, expireDay, key.getStorageType(), this.request);
        if (result.isNotSuccess()) {
            return Result.error(result.getMessage());
        }
        Image image = result.getData();
        image.setUpdatetime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        image.setSource(key.getStorageType());
        image.setUserId(user != null ? user.getId() : UserConstant.TOURIST_ID);
        image.setAbnormal(0);
        this.imageService.insert(image);
        return Result.success(image);
    }


    /**
     * 根据网络图片url上传
     */
    @PostMapping("/up-url")
    @ResponseBody
    public Result<?> uploadByUrl(String imageUrl, Integer expireDay) throws Exception {
        if (!super.isLogin()) {
            return Result.error("请先登录");
        }
        User user = super.getCurrentLoginUser();
        // Result<Boolean> booleanResult = this.storageHandler.checkPreservable(imageUrl, user);
        // if (booleanResult.isNotSuccess()) {
        //     return booleanResult;
        // }
        Key key = this.keyService.getCurrentKey(user);
        Result<Image> result = this.storageHandler.saveHand(imageUrl, expireDay, key.getStorageType(), this.request);
        if (result.isNotSuccess()) {
            return Result.error(result.getMessage());
        }
        Image image = result.getData();
        image.setUpdatetime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        image.setSource(key.getStorageType());
        image.setUserId(user != null ? user.getId() : 0L);
        image.setAbnormal(0);
        this.imageService.insert(image);
        return Result.success(image);
    }


    @PostMapping("/admin/deleimg")
    @ResponseBody
    public String delete(long id, Integer sourcekey, HttpSession session) {
        JSONObject jsonObject = new JSONObject();
        User u = (User) session.getAttribute("user");
        Images images = imageService.selectByPrimaryKey((int) id);
        Key key = keyService.selectByStorageType(sourcekey);
        int Sourcekey = this.keyService.getCurrentKey(u).getStorageType();
        boolean b = false;
        if (Sourcekey == 5) {
            b = true;
        } else {
            b = StringUtils.doNull(Sourcekey, key);
        }
        if (b) {
            ImageServiceImpl de = new ImageServiceImpl();
            if (key.getStorageType() == 1) {
                de.delect(key, images.getImgname());
            } else if (key.getStorageType() == 2) {
                de.delectOSS(key, images.getImgname());
            } else if (key.getStorageType() == 3) {
                de.delectUSS(key, images.getImgname());
            } else if (key.getStorageType() == 4) {
                de.delectKODO(key, images.getImgname());
            } else if (key.getStorageType() == 5) {
                LocUpdateImg.deleteLOCImg(images.getImgname());
            } else if (key.getStorageType() == 6) {
                de.delectCOS(key, images.getImgname());
            } else if (key.getStorageType() == 7) {
                de.delectFTP(key, images.getImgname());
            } else {
                System.err.println("未获取到对象存储参数，删除失败。");
            }
            Integer ret = imageService.delete(id);
            int count = 0;
            if (ret > 0) {
                jsonObject.put("usercount", imageService.countimg(u.getId()));
                jsonObject.put("count", imageService.counts(null));
                count = 1;
            } else {
                count = 0;
            }
            jsonObject.put("val", count);
        } else {
            jsonObject.put("val", 0);
        }
        return jsonObject.toString();
    }


}
