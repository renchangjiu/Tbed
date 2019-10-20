package com.su.controller;

import java.io.FileInputStream;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSONArray;

/**
 * @author su
 * @date 2019/10/19 12:07
 */
@Controller
public class ImageController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private NOSImageupload nOSImageupload;
    @Autowired
    private UserService userService;
    @Autowired
    private KeysService keysService;
    @Autowired
    private OSSImageupload ossImageupload;
    @Autowired
    private ConfigService configService;
    @Autowired
    private UploadConfigService uploadConfigService;
    @Autowired
    private USSImageupload ussImageupload;
    @Autowired
    private KODOImageupload kodoImageupload;
    @Autowired
    private COSImageupload cosImageupload;
    @Autowired
    private FTPImageupload ftpImageupload;
    @Autowired
    private ImageService imageService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private StorageHandler storageHandler;
    @Autowired
    private SystemConfig systemConfig;

    @RequestMapping({"/", "/index"})
    public String indexImg(ModelMap map, HttpSession httpSession) {
        //查询当前系统使用的存储源类型。
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
        User user = (User) httpSession.getAttribute("user");
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
        map.addAttribute("uploadConfig", uploadConfig);

        int uploadable = (this.systemConfig.touristUploadable || user != null) ? 1 : 0;
        map.put("uploadable", uploadable);
        return "index";

    }

    @RequestMapping("/up")
    @ResponseBody
    public Result<?> upload(@RequestParam(value = "file", required = false) MultipartFile file, Integer setday,
                            HttpSession session) {
        User user = (User) session.getAttribute("user");
        Result<Boolean> booleanResult = this.storageHandler.canSave(user);
        if (booleanResult.isNotSuccess()) {
            return booleanResult;
        }
        Result<Group> res = this.groupService.getByUserId(user != null ? user.getId() : null);
        int storageType = res.getData().getKeyid();
        Keys key = keysService.selectByStorageType(storageType);
        Result<Image> result = this.storageHandler.saveHand(file, setday, key.getStorageType(), this.request);
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


    /**
     * 根据网络图片url上传
     */
    @PostMapping("/up-url")
    @ResponseBody
    public String uploadByUrl(HttpSession session, String imgurl, HttpServletRequest request, Integer setday) throws Exception {
        Config config = configService.getSourceype();//查询当前系统使用的存储源类型。
        UploadConfig uploadConfig = uploadConfigService.getUpdateConfig();
        User u = (User) session.getAttribute("user");

        Integer usermemory = 0;
        Integer memory = 0;
        Integer Sourcekey = 0;
        if (u == null) {
            Sourcekey = GetCurrentSource.GetSource(null);
            memory = uploadConfig.getVisitormemory();
            usermemory = imageService.getUsedMemory(0L);
            if (usermemory == null) {
                usermemory = 0;
            }
        } else {
            Sourcekey = GetCurrentSource.GetSource(u.getId());
            memory = userService.getUsers(u.getEmail()).getMemory();
            usermemory = imageService.getUsedMemory(u.getId());
            if (usermemory == null) {
                usermemory = 0;
            }
        }
        String userpath = "tourist";
        if (uploadConfig.getUrltype() == 2) {
            java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd");
            userpath = dateFormat.format(new Date());
        } else {
            if (u != null) {
                userpath = u.getUsername();
            }
        }
        JSONArray jsonArray = new JSONArray();

        Keys key = keysService.selectByStorageType(Sourcekey);
        long imgsize = ImgUrlUtil.getFileLength(imgurl);
        Integer youke = uploadConfig.getFilesizetourists();
        Integer yonghu = uploadConfig.getFilesizeuser();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        boolean bo = false;
        if (Sourcekey == 5) {
            bo = true;
        } else {
            bo = StringUtils.doNull(Sourcekey, key);
        }
//        //容量判断
//        Integer usermemory =0;
//        Integer memory =0;
        if (u == null) {
            memory = uploadConfig.getVisitormemory();
            usermemory = imageService.getUsedMemory(0L);
            if (usermemory == null) {
                usermemory = 0;
            }
        } else {
            memory = userService.getUsers(u.getEmail()).getMemory();
            usermemory = imageService.getUsedMemory(u.getId());
            if (usermemory == null) {
                usermemory = 0;
            }
        }
        //先判断对象存储key是不是null
        Print.warning("上传地址是：" + request.getSession().getServletContext().getRealPath("/") + "/hellohaotmp/");
        if (bo) {
            if (usermemory / 1024 < memory) {
                long stime = System.currentTimeMillis();
                //判断是会员还是游客
                if (u != null) {
                    //判断文件大小
                    if (imgsize > 0 && imgsize <= (yonghu * 1024 * 1024)) {
                        try {
                            boolean bl = ImgUrlUtil.downLoadFromUrl(imgurl,
                                    uuid, request.getSession().getServletContext().getRealPath("/") + "/hellohaotmp/");
                            if (bl == true) {
                                FileInputStream is = new FileInputStream(request.getSession().getServletContext().getRealPath("/") + "/hellohaotmp/" + uuid);
                                byte[] b = new byte[3];
                                is.read(b, 0, b.length);
                                String xxx = ImgUrlUtil.bytesToHexString(b);
                                xxx = xxx.toUpperCase();
                                System.out.println("头文件是：" + xxx);
                                String ooo = TypeDict.checkType(xxx);
                                System.out.println("后缀名是：" + ooo);
                                if (is != null) {
                                    is.close();
                                }
                                //判断文件头是否是图片
                                if (!ooo.equals("0000")) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put(ooo, request.getSession().getServletContext().getRealPath("/") + "/hellohaotmp/" + uuid);
                                    Map<ReturnImage, Integer> m = null;
                                    if (key.getStorageType() == 1) {
                                        m = nOSImageupload.Imageupload(null, userpath, map, setday);
                                    } else if (key.getStorageType() == 2) {
                                        m = ossImageupload.ImageuploadOSS(null, userpath, map, setday);
                                    } else if (key.getStorageType() == 3) {
                                        m = ussImageupload.ImageuploadUSS(null, userpath, map, setday);
                                    } else if (key.getStorageType() == 4) {
                                        m = kodoImageupload.ImageuploadKODO(null, userpath, map, setday);
                                    } else if (key.getStorageType() == 5) {
                                        m = LocUpdateImg.ImageuploadLOC(null, userpath, map, setday);
                                    } else if (key.getStorageType() == 6) {
                                        m = cosImageupload.ImageuploadCOS(null, userpath, map, setday);
                                    } else if (key.getStorageType() == 7) {
                                        m = ftpImageupload.ImageuploadFTP(null, userpath, map, setday);
                                    } else {
                                        System.err.println("未获取到对象存储参数，上传失败。");
                                    }
                                    Images img = new Images();
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                    String times = df.format(new Date());
                                    System.out.println("上传图片的时间是：" + times);
                                    for (Map.Entry<ReturnImage, Integer> entry : m.entrySet()) {
                                        if (key.getStorageType() == 5) {
                                            if (config.getDomain() != null) {
                                                jsonArray.add(config.getDomain() + "/links/" + entry.getKey().getImgurl());
                                                img.setImgurl(config.getDomain() + "/links/" + entry.getKey().getImgurl());//图片链接
                                            } else {
                                                jsonArray.add(config.getDomain() + "/links/" + entry.getKey().getImgurl());
                                                img.setImgurl("http://" + IPPortUtil.getLocalIP() + ":" + IPPortUtil.getLocalPort() + "/links/" + entry.getKey().getImgurl());//图片链接
                                            }
                                        } else {
                                            jsonArray.add(entry.getKey().getImgurl());
                                            img.setImgurl(entry.getKey().getImgurl());
                                        }
                                        img.setUpdatetime(times);
                                        img.setSource(key.getStorageType());
                                        if (u == null) {
                                            img.setUserid(0L);//用户id
                                        } else {
                                            img.setUserid(u.getId());//用户id
                                        }
                                        img.setSizes((entry.getValue()));
                                        img.setImgname(SetText.getSubString(entry.getKey().getImgurl(), key.getRequestAddress() + "/", ""));
                                        img.setAbnormal(0);
                                        // todo
                                        // userService.insertimg(img);
                                        long etime = System.currentTimeMillis();
                                        System.out.println("上传图片所用时长：" + String.valueOf(etime - stime) + "ms");
                                    }
                                } else {
                                    jsonArray.add(-3);
                                }
                            }
                        } catch (Exception e) {
                            Print.warning(e.toString());
                            jsonArray.add(-4);
                        }
                    } else {
                        //文件过大
                        jsonArray.add(-2);
                    }
                } else {
                    if (imgsize > 0 && imgsize <= (youke * 1024 * 1024)) {
                        try {
                            boolean bl = ImgUrlUtil.downLoadFromUrl(imgurl,
                                    uuid, request.getSession().getServletContext().getRealPath("/") + "/hellohaotmp/");
                            if (bl == true) {
                                FileInputStream is = new FileInputStream(request.getSession().getServletContext().getRealPath("/") + "/hellohaotmp/" + uuid);
                                byte[] b = new byte[3];
                                is.read(b, 0, b.length);
                                String xxx = ImgUrlUtil.bytesToHexString(b);
                                xxx = xxx.toUpperCase();
                                //System.out.println("头文件是：" + xxx);
                                String ooo = TypeDict.checkType(xxx);
                                //System.out.println("后缀名是：" + ooo);
                                if (is != null) {
                                    is.close();
                                }
                                //判断文件头是否是图片
                                if (!xxx.equals("0000")) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put(ooo, request.getSession().getServletContext().getRealPath("/") + "/hellohaotmp/" + uuid);
                                    Map<ReturnImage, Integer> m = null;
                                    if (key.getStorageType() == 1) {
                                        m = nOSImageupload.Imageupload(null, userpath, map, setday);
                                    } else if (key.getStorageType() == 2) {
                                        m = ossImageupload.ImageuploadOSS(null, userpath, map, setday);
                                    } else if (key.getStorageType() == 3) {
                                        m = ussImageupload.ImageuploadUSS(null, userpath, map, setday);
                                    } else if (key.getStorageType() == 4) {
                                        m = kodoImageupload.ImageuploadKODO(null, userpath, map, setday);
                                    } else if (key.getStorageType() == 5) {
                                        m = LocUpdateImg.ImageuploadLOC(null, userpath, map, setday);
                                    } else if (key.getStorageType() == 6) {
                                        m = cosImageupload.ImageuploadCOS(null, userpath, map, setday);
                                    } else if (key.getStorageType() == 7) {
                                        m = ftpImageupload.ImageuploadFTP(null, userpath, map, setday);
                                    } else {
                                        System.err.println("未获取到对象存储参数，上传失败。");
                                    }
                                    Images img = new Images();
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                    String times = df.format(new Date());
                                    System.out.println("上传图片的时间是：" + times);
                                    for (Map.Entry<ReturnImage, Integer> entry : m.entrySet()) {
                                        if (key.getStorageType() == 5) {
                                            if (config.getDomain() != null) {
                                                jsonArray.add(config.getDomain() + "/links/" + entry.getKey().getImgurl());
                                                img.setImgurl(config.getDomain() + "/links/" + entry.getKey().getImgurl());//图片链接
                                            } else {
                                                jsonArray.add(config.getDomain() + "/links/" + entry.getKey().getImgurl());
                                                img.setImgurl("http://" + IPPortUtil.getLocalIP() + ":" + IPPortUtil.getLocalPort() + "/links/" + entry.getKey().getImgurl());//图片链接
                                            }
                                        } else {
                                            jsonArray.add(entry.getKey().getImgurl());
                                        }
                                        //img.setImgurl(entry.getKey());//图片链接
                                        img.setUpdatetime(times);
                                        img.setSource(key.getStorageType());
                                        if (u == null) {
                                            img.setUserid(0L);//用户id
                                        } else {
                                            img.setUserid(u.getId());//用户id
                                        }
                                        img.setSizes((entry.getValue()));
                                        img.setImgname(SetText.getSubString(entry.getKey().getImgurl(), key.getRequestAddress() + "/", ""));
                                        img.setAbnormal(0);
                                        // todo
                                        // userService.insertimg(img);
                                        long etime = System.currentTimeMillis();
                                        System.out.println("上传图片所用时长：" + String.valueOf(etime - stime) + "ms");
                                    }
                                } else {
                                    jsonArray.add(-3);
                                }
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            Print.warning(e.toString());
                            jsonArray.add(-4);
                        }
                    } else {
                        //文件过大
                        jsonArray.add(-2);
                    }
                }
            } else {
                jsonArray.add(-5);
            }
        } else {
            jsonArray.add(-1);
        }
        return jsonArray.toString();
/**
 * 错误返回值含义：
 * -1 存储源key未配置
 * -2 目标图片太大或者不存在
 * -3 文件类型不符合要求
 * */
    }


    @PostMapping("/admin/deleimg")
    @ResponseBody
    public String delete(long id, Integer sourcekey, HttpSession session) {
        JSONObject jsonObject = new JSONObject();
        User u = (User) session.getAttribute("user");
        Images images = imageService.selectByPrimaryKey((int) id);
        Keys key = keysService.selectByStorageType(sourcekey);
        Integer Sourcekey = GetCurrentSource.GetSource(u.getId());
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

    //刪除用戶
    @RequestMapping("/sentence")
    @ResponseBody
    public String sentence(HttpSession session, Integer id) {
        JSONArray jsonArray = new JSONArray();
        String text = Sentence.getURLContent();
        jsonArray.add(text);
        return jsonArray.toString();
    }

    //ajax查询用户是否已经登录
    @RequestMapping(value = "/islogin")
    @ResponseBody
    public String islogin(HttpSession session) {
        JSONObject jsonObject = new JSONObject();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            if (user.getEmail() != null && user.getPassword() != null) {
                jsonObject.put("username", user.getUsername());
                jsonObject.put("level", user.getLevel());
                jsonObject.put("lgoinret", 1);
            } else {
                jsonObject.put("lgoinret", 0);
            }
        }
        return jsonObject.toString();
    }

    @RequestMapping("/err")
    public String err() {
        return "err";
    }

}
