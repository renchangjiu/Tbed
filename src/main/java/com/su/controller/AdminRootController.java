package com.su.controller;

import com.su.pojo.*;
import com.su.service.*;
import com.su.service.impl.*;
import com.su.utils.StringUtils;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

@Controller
@RequestMapping("/admin/root")
public class AdminRootController {
    @Autowired
    private NOSImageupload nOSImageupload;
    @Autowired
    private ConfigService configService;
    @Autowired
    private KeyService keyService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailConfigService emailConfigService;
    @Autowired
    private UploadConfigService uploadConfigService;
    @Autowired
    private NoticeService noticeService;

    @Value("${systemupdate}")
    private String systemupdate;

    //返回对象存储界面
    @RequestMapping(value = "tostorage")
    public String tostorage(HttpSession session, Model model, HttpServletRequest request) {
        User u = (User) session.getAttribute("user");
        Integer Sourcekey = this.keyService.getCurrentKey(u).getStorageType();
        Key key = keyService.selectByStorageType(Sourcekey);//然后根据类型再查询key
        Boolean b = StringUtils.doNull(Sourcekey, key);//判断对象是否有空值
        Integer StorageType = 0;
        if (Sourcekey != 5) {
            if (b) {
                //key信息
                model.addAttribute("AccessKey", key.getAccessKey());
                model.addAttribute("AccessSecret", key.getAccessSecret());
                model.addAttribute("Endpoint", key.getEndpoint());
                model.addAttribute("Bucketname", key.getBucketname());
                model.addAttribute("RequestAddress", key.getRequestAddress());
                model.addAttribute("StorageType", Sourcekey);
            }
            //如果是4就是七牛
            if (Sourcekey == 4) {
                model.addAttribute("Endpoint2", key.getEndpoint());
            } else {
                model.addAttribute("Endpoint2", 0);
            }
        } else {
            model.addAttribute("StorageType", 5);//切换到本地
            model.addAttribute("Endpoint2", 0);
        }


        return "admin/storageconfig";
    }


    @PostMapping("/getkey")
    @ResponseBody
    public String getkey(Integer storageType) {
        JSONArray jsonArray = new JSONArray();
        Key key = keyService.selectByStorageType(storageType);
        jsonArray.add(key);
        return jsonArray.toString();
    }

    @PostMapping("/getkeyourceype")
    @ResponseBody
    public Integer getkeyourceype(HttpSession session) {
        User u = (User) session.getAttribute("user");
        Integer Sourcekey = this.keyService.getCurrentKey(u).getStorageType();
        Integer ret = 0;
        if (Sourcekey != null) {
            ret = Sourcekey;
        }
        return ret;
    }

    @PostMapping("/updatekey")
    @ResponseBody
    public String updatekey(Key key) {
        JSONArray jsonArray = new JSONArray();
        Config config = new Config();
        config.setSourcekey(key.getStorageType());
        Integer val = configService.setSourceype(config);
        //if (val > 0) {
        Integer ret = -2;
        //修改完初始化
        if (key.getStorageType() == 1) {
            ret = nOSImageupload.Initialize(key);//实例化网易
        } else if (key.getStorageType() == 2) {
            ret = OSSImageupload.Initialize(key);
        } else if (key.getStorageType() == 3) {
            ret = USSImageupload.Initialize(key);
        } else if (key.getStorageType() == 4) {
            ret = KODOImageupload.Initialize(key);
        } else if (key.getStorageType() == 6) {
            ret = COSImageupload.Initialize(key);
        } else if (key.getStorageType() == 7) {
            ret = FTPImageupload.Initialize(key);
        } else {
            // Print.Normal("为获取到存储参数，或者使用存储源是本地的。");
        }
        if (ret > 0) {
            ret = keyService.updateKey(key);
        }

        //-1 对象存储有参数为空,初始化失败
        //0，保存失败
        //1 正确
        jsonArray.add(ret);
        //} else {
        //jsonArray.add(0);
        // }
        return jsonArray.toString();
    }



    @RequestMapping(value = "/towebconfig")
    public String towebconfig(HttpSession session, Model model) {
        Config config = configService.getSourceype();
        User u = (User) session.getAttribute("user");
        Integer Sourcekey = this.keyService.getCurrentKey(u).getStorageType();
        UploadConfig updateConfig = uploadConfigService.getUpdateConfig();
        model.addAttribute("config", config);
        model.addAttribute("updateConfig", updateConfig);
        model.addAttribute("group", Sourcekey);
        return "admin/webconfig";
    }

    //修改站点配置
    @PostMapping("/updateconfig")
    @ResponseBody
    public Integer updateconfig(Config config) {
        return configService.setSourceype(config);
    }

    //修改上传配置
    @PostMapping("/scconfig")
    @ResponseBody
    public Integer scconfig(UploadConfig updateConfig) {
        Integer ret = uploadConfigService.setUpdateConfig(updateConfig);
        return ret;
    }




    @PostMapping("/settstoragetype")
    @ResponseBody
    public Integer settstoragetype(Integer storagetype) {
        Config config = new Config();
        config.setSourcekey(storagetype);
        Integer val = configService.setSourceype(config);
        return val;
    }


    //关于系统
    @RequestMapping("/about")
    public String about(HttpSession session, Model model) {
        //Integer ret = uploadConfigService.setUpdateConfig(updateConfig);
        User u = (User) session.getAttribute("user");
        model.addAttribute("level", u.getLevel());
        model.addAttribute("systemupdate", systemupdate);
        return "admin/about";
    }

    //检查更新
    @PostMapping("/sysupdate")
    @ResponseBody
    public Integer sysupdate(String dates) {
        HashMap<String, Object> paramMap = new HashMap<>();
        String urls = "http://tc.hellohao.cn/systemupdate";
        paramMap.put("dates", dates);
        String result = HttpUtil.post(urls, paramMap);
        System.out.println(Integer.parseInt(result));
        return Integer.parseInt(result);
    }

}
