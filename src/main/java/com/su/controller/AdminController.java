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
public class AdminController {

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
    @Autowired
    private CodeService codeService;


    @RequestMapping(value = "/goadmin")
    public String goadmin1(HttpSession session, Model model, HttpServletRequest request) {
        User user = (User) session.getAttribute("user");
        UploadConfig uploadConfig = uploadConfigService.getUpdateConfig();
        Integer usermemory = imageService.getUsedMemory(user.getId());
        if (usermemory == null) {
            usermemory = 0;
        }
        User u = userService.getUsers(user.getEmail());
        if (user != null) {
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
            if (usermemory == null) {
                model.addAttribute("usermemory", 0);//单位M
            } else {
                float d = (float) (Math.round((usermemory / 1024.0F) * 100.0) / 100.0);
                //Print.Normal();
                model.addAttribute("usermemory", d);//单位M
            }
            return "admin/index";
        } else {
            return "redirect:/";
        }
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
        Integer usermemory = imageService.getUsedMemory(u.getId());
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
        Config config = configService.getSourceype();
        User u = (User) session.getAttribute("user");
        // Integer Sourcekey = GetCurrentSource.GetSource(u.getId());
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


    @RequestMapping(value = "/selecttable")
    @ResponseBody
    public PageResultBean<Images> selectByFy(HttpSession session, Integer pageNum, Integer pageSize, Integer selecttype,
                                             Integer storageType, String starttime, String stoptime) {
        User u = (User) session.getAttribute("user");
        Images img = new Images();
        if (storageType != null) {
            if (storageType != 0) {
                img.setSource(storageType);
            }
        }
        if (starttime != null && stoptime != null) {
            if (!starttime.equals("") && !stoptime.equals("")) {
                img.setStarttime(starttime);
                img.setStoptime(stoptime);
            }
        }
        // 使用Pagehelper传入当前页数和页面显示条数会自动为我们的select语句加上limit查询
        // 从他的下一条sql开始分页
        PageHelper.startPage(pageNum, pageSize);
        List<Images> images = null;
        if (u.getLevel() > 1) { //根据用户等级查询管理员查询所有的信息
            if (selecttype == 1) {
                images = imageService.selectimg(img);// 这是我们的sql
            } else {
                img.setUserid(u.getId());
                images = imageService.selectimg(img);// 这是我们的sql
            }
        } else {
            img.setUserid(u.getId());
            images = imageService.selectimg(img);// 这是我们的sql
        }
        // 使用pageInfo包装查询
        PageInfo<Images> rolePageInfo = new PageInfo<>(images);//
        return new PageResultBean<>(rolePageInfo.getTotal(), rolePageInfo.getList());
    }


    //获取用户信息列表
    @RequestMapping(value = "/selectusertable")
    @ResponseBody
    public Map<String, Object> selectByFy12(HttpSession session, @RequestParam(required = false, defaultValue = "1") int page,
                                            @RequestParam(required = false) int limit, String username) {
        User u = (User) session.getAttribute("user");
        PageHelper.startPage(page, limit);
        List<User> users = null;
        if (u.getLevel() > 1) { //根据用户等级查询管理员查询所有的信息
            User user = new User();
            user.setUsername(username);
            users = userService.getuserlist(user);// 这是我们的sql
            // 使用pageInfo包装查询
            PageInfo<User> rolePageInfo = new PageInfo<>(users);//
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("code", 0);
            map.put("msg", "");
            map.put("count", rolePageInfo.getTotal());
            map.put("data", rolePageInfo.getList());
            return map;
        } else {
            return null;
        }
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

    //进入修改密码页面
    @RequestMapping(value = "/tosetuser")
    public String tosetuser(HttpSession session, Model model, HttpServletRequest request) {
        User u = (User) session.getAttribute("user");
        //key信息
        model.addAttribute("user", u);
        return "admin/setuser";
    }

    //修改资料
    @PostMapping("/change")
    @ResponseBody
    public String change(HttpSession session, User user) {
        User u = (User) session.getAttribute("user");
        User us = new User();
        us.setEmail(user.getEmail());
        us.setUsername(user.getUsername());
        us.setPassword(new String(Base64.encodeBase64(user.getPassword().getBytes())));
        us.setUid(u.getUid());
        //user.setUid(u.getUid());
        JSONArray jsonArray = new JSONArray();
        Integer ret = 0;
        if (u.getLevel() == 1) {
            us.setUsername(null);
            us.setEmail(null);
            ret = userService.change(us);
        } else {
            ret = userService.change(us);
        }
        jsonArray.add(ret);
        if (u.getEmail() != null && u.getPassword() != null) {
            session.removeAttribute("user");
            //刷新view
            session.invalidate();
        }
        // -1 用户名重复
        return jsonArray.toString();
    }

    //进入api
    @RequestMapping(value = "/toapi")
    public String toapi(HttpSession session, Model model, HttpServletRequest request) {
        User u = (User) session.getAttribute("user");
        Config config = configService.getSourceype();
        //key信息
        model.addAttribute("username", u.getUsername());
        model.addAttribute("level", u.getLevel());
        model.addAttribute("domain", config.getDomain());
        return "admin/api";
    }


    @PostMapping(value = "/kuorong")
    @ResponseBody
    public String kuorong(HttpSession session, String codestring) {
        User u = (User) session.getAttribute("user");
        JSONObject jsonObject = new JSONObject();
        User u1 = userService.getUsers(u.getEmail());
        Integer ret = 0;
        Integer sizes = 0;
        if (u != null) {
            //List<Code> code = codeService.selectCode(codestring);
            Code c = codeService.selectCodekey(codestring);
            if (c != null) {
                User user = new User();
                sizes = c.getValue() + u1.getMemory();
                user.setMemory(c.getValue() + u1.getMemory());
                user.setId(u.getId());
                ret = userServiceImpl.usersetmemory(user, codestring);
                if (ret > 0) {
                    ret = 1;
                }
            } else {
                //扩容码不存在
                ret = -1;
            }
        } else {
            //登录过期
            session.invalidate();
        }
        jsonObject.put("sizes", sizes);
        jsonObject.put("ret", ret);
        return jsonObject.toString();
    }


    @GetMapping(value = "/images/{id}")
    @ResponseBody
    public Images selectByFy(@PathVariable("id") Integer id) {
        return imageService.selectByPrimaryKey(id);
    }


}
