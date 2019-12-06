package com.su.controller;

import com.su.pojo.*;
import com.su.pojo.vo.PageResultBean;
import com.su.service.*;
import com.su.service.impl.ImageServiceImpl;
import com.su.service.impl.UserServiceImpl;
import com.su.utils.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hellohao
 * @version 1.0
 * @date 2019-07-17 14:22
 */
@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private KeyService keyService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private ImgreviewService imgreviewService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private UploadConfigService uploadConfigService;


    @RequestMapping("/goadmin")
    public String goadmin1(HttpSession session, Model model) {
        if (!super.isLogin()) {
            return "redirect:/";
        }
        User user = (User) session.getAttribute("user");
        UploadConfig uploadConfig = uploadConfigService.getUpdateConfig();
        Integer usermemory = imageService.getUsedMemory(user);
        if (usermemory == null) {
            usermemory = 0;
        }
        User u = userService.getUsers(user.getEmail());
        if (user.getLevel() == 1) {
            model.addAttribute("level", "普通用户");
        } else if (user.getLevel() == 2) {
            model.addAttribute("level", "管理员");
        } else {
            model.addAttribute("level", "未 知");
        }
        model.addAttribute("levels", user.getLevel());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("api", uploadConfig.getApi());

        model.addAttribute("memory", u.getMemory());//单位M
        float d = (float) (Math.round((usermemory / 1024.0F) * 100.0) / 100.0);
        model.addAttribute("usermemory", d);//单位M
        return "admin/index";
    }

    @RequestMapping(value = "/admin")
    public String goadmin(HttpSession session, Model model) {
        Config config = configService.getSourceype();
        User u = (User) session.getAttribute("user");
        model.addAttribute("username", u.getUsername());
        model.addAttribute("level", u.getLevel());
        model.addAttribute("email", u.getEmail());
        model.addAttribute("loginid", 100);

        return "admin/table";
    }

    @RequestMapping(value = "/tosurvey")
    public String admin2(HttpSession session, Model model) {
        User u = (User) session.getAttribute("user");
        String sysetmname = System.getProperty("os.name");
        String isarch = System.getProperty("os.arch");
        String jdk = System.getProperty("java.version");
        model.addAttribute("username", u.getUsername());
        model.addAttribute("levels", u.getLevel());
        model.addAttribute("sysetmname", sysetmname);
        model.addAttribute("isarch", isarch);
        model.addAttribute("jdk", jdk);

        //空间大小
        UploadConfig uploadConfig = uploadConfigService.getUpdateConfig();
        Integer usermemory = imageService.getUsedMemory(u);
        if (usermemory == null) {
            usermemory = 0;
        }
        User user = userService.getUsers(u.getEmail());
        if (u != null) {
            if (u.getLevel() == 1) {
                model.addAttribute("level", "普通用户");
            } else if (u.getLevel() == 2) {
                model.addAttribute("level", "管理员");
            } else {
                model.addAttribute("level", "未 知");
            }
            model.addAttribute("levels", u.getLevel());
            model.addAttribute("username", u.getUsername());
            model.addAttribute("api", uploadConfig.getApi());

            model.addAttribute("memory", user.getMemory());//单位M
            if (usermemory == null) {
                model.addAttribute("usermemory", 0);//单位M
            } else {
                float d = (float) (Math.round((usermemory / 1024.0F) * 100.0) / 100.0);
                //Print.Normal();
                model.addAttribute("usermemory", d);//单位M
            }
        }
        return "admin/survey";
    }

    @RequestMapping(value = "/getwebconfig")
    @ResponseBody
    public String getwebconfig(HttpSession session) {
        JSONObject jsonObject = new JSONObject();
        User u = (User) session.getAttribute("user");
        Integer Sourcekey = this.keyService.getCurrentKey(u).getStorageType();
        Imgreview imgreview = imgreviewService.selectByPrimaryKey(1);
        jsonObject.put("usercount", imageService.countimg(u.getId()));
        jsonObject.put("counts", imageService.counts(null));
        jsonObject.put("getusertotal", userService.getUserTotal());
        jsonObject.put("imgreviewcount", imgreview.getCount());
        Key key = keyService.selectByStorageType(Sourcekey);
        Boolean b = false;
        if (Sourcekey == 5) {
            b = true;
        } else {
            b = StringUtils.doNull(Sourcekey, key);//判断对象是否有空值
        }
        if (b) {
            jsonObject.put("source", key.getStorageType());
        } else {
            jsonObject.put("source", 456);
        }
        return jsonObject.toString();
    }


    @RequestMapping("/selecttable")
    @ResponseBody
    public PageResultBean<Image> selectByFy(HttpSession session, Integer pageNum, Integer pageSize, Integer selecttype,
                                            Integer storageType, String starttime, String stoptime) {
        User user = super.getCurrentLoginUser();
        Image img = new Image();
        if (storageType != null) {
            if (storageType != 0) {
                img.setSource(storageType);
            }
        }
        if (starttime != null && stoptime != null) {
            if (!"".equals(starttime) && !"".equals(stoptime)) {
                img.setStarttime(starttime);
                img.setStoptime(stoptime);
            }
        }
        // 使用Pagehelper传入当前页数和页面显示条数会自动为我们的select语句加上limit查询
        // 从他的下一条sql开始分页
        PageHelper.startPage(pageNum, pageSize);
        List<Image> images = null;
        if (user.getLevel() > 1) { //根据用户等级查询管理员查询所有的信息
            if (selecttype == 1) {
                images = imageService.listData(img);
            } else {
                img.setUserId(user.getId());
                images = imageService.listData(img);
            }
        } else {
            img.setUserId(user.getId());
            images = imageService.listData(img);
        }
        // 使用pageInfo包装查询
        PageInfo<Image> rolePageInfo = new PageInfo<>(images);//
        return new PageResultBean<>(rolePageInfo.getTotal(), rolePageInfo.getList());
    }



    //批量删除图片
    @PostMapping("/deleallimg")
    @ResponseBody
    public String deleallimg(HttpSession session, @RequestParam("ids[]") Long[] ids) {
        JSONObject jsonObject = new JSONObject();

        Integer v = 0;
        ImageServiceImpl de = new ImageServiceImpl();
        User u = (User) session.getAttribute("user");
        Integer Sourcekey = this.keyService.getCurrentKey(u).getStorageType();
        for (int i = 0; i < ids.length; i++) {
            Images images = imageService.selectByPrimaryKey(Integer.parseInt(ids[i] + ""));
            Key key = keyService.selectByStorageType(images.getSource());
            Boolean b = false;
            if (Sourcekey == 5) {
                b = true;
            } else {
                b = StringUtils.doNull(Sourcekey, key);//判断对象是否有空值
            }
            if (b) {
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
                Integer ret = imageService.delete(ids[i]);
                if (ret < 1) {
                    v = 0;
                } else {
                    v = 1;
                }
            } else {
                v = 0;
            }
        }
        jsonObject.put("val", v);
        jsonObject.put("usercount", imageService.countimg(u.getId()));
        jsonObject.put("count", imageService.counts(null));
        return jsonObject.toString();
    }



    @GetMapping(value = "/images/{id}")
    @ResponseBody
    public Images selectByFy(@PathVariable("id") Integer id) {
        return imageService.selectByPrimaryKey(id);
    }


}
