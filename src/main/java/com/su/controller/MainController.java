package com.su.controller;

import com.su.config.SystemConfig;
import com.su.pojo.Config;
import com.su.pojo.UploadConfig;
import com.su.pojo.User;
import com.su.service.ConfigService;
import com.su.service.UploadConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author su
 * @date 2019/10/24 10:27
 */
@Controller
public class MainController extends BaseController {

    @Autowired
    private ConfigService configService;
    @Autowired
    private UploadConfigService uploadConfigService;

    @Autowired
    private SystemConfig systemConfig;

    @RequestMapping({"/", "/index"})
    public String indexImg(ModelMap map) {
        Config config = configService.getSourceype();
        UploadConfig uploadConfig = uploadConfigService.getUpdateConfig();
        Integer filesizetourists = 0;
        Integer filesizeuser = 0;
        Integer imgcounttourists = 0;
        Integer imgcountuser = 0;
        if (uploadConfig.getFilesizetourists() != null) {
            filesizetourists = uploadConfig.getFilesizetourists();
        }
        if (uploadConfig.getFilesizeuser() != null) {
            filesizeuser = uploadConfig.getFilesizeuser();
        }
        if (uploadConfig.getImgcounttourists() != null) {
            imgcounttourists = uploadConfig.getImgcounttourists();
        }
        if (uploadConfig.getImgcountuser() != null) {
            imgcountuser = uploadConfig.getImgcountuser();
        }
        User user = super.getCurrentLoginUser();
        if (user != null) {
            map.addAttribute("username", user.getUsername());
            map.addAttribute("level", user.getLevel());
            map.addAttribute("imgcount", imgcountuser);
            map.addAttribute("filesize", filesizeuser * 1024 * 1024);
        } else {
            map.addAttribute("imgcount", imgcounttourists);
            map.addAttribute("filesize", filesizetourists * 1024 * 1024);
        }
        map.addAttribute("suffix", uploadConfig.getSuffix());
        map.addAttribute("config", config);

        int uploadable = (this.systemConfig.getTouristUploadable() || user != null) ? 1 : 0;
        map.put("uploadable", uploadable);
        map.put("systemConfig", this.systemConfig);
        return "index";

    }

    @RequestMapping("/err")
    public String err() {
        return "err";
    }
}
