package cn.hellohao.controller;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.hellohao.pojo.*;
import cn.hellohao.service.*;
import cn.hellohao.service.impl.*;
import cn.hellohao.utils.IPPortUtil;
import cn.hellohao.utils.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class UpdateImgController {

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

    @Value("${system.enable-https}")
    private String enableHttps;

    @RequestMapping({"/", "/index"})
    public String indexImg(Model model, HttpSession httpSession) {
        //boolean b = VerificationDomain.verification();
        Print.Normal("欢迎使用Hellohao图床源码。者也许是最好用的java图床项目。");
        Print.Normal("当前项目路径：" + System.getProperty("user.dir"));
        Config config = configService.getSourceype();//查询当前系统使用的存储源类型。
        UploadConfig uploadConfig = uploadConfigService.getUpdateConfig();
        User u = (User) httpSession.getAttribute("user");
        String email = (String) httpSession.getAttribute("email");
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
        //Boolean b =false;
        //Integer Sourcekey = GetCurrentSource.GetSource(u==null?null:u.getId());//查询当当前用户或者游客使用的数据源
        if (email != null) {
            //登陆成功
            Integer ret = userService.login(u.getEmail(), u.getPassword());
            if (ret > 0) {
                User user = userService.getUsers(u.getEmail());
                model.addAttribute("username", user.getUsername());
                model.addAttribute("level", user.getLevel());
                model.addAttribute("loginid", 100);
                model.addAttribute("imgcount", imgcountuser);
                model.addAttribute("filesize", filesizeuser * 1024 * 1024);

            } else {
                model.addAttribute("loginid", -1);
                model.addAttribute("imgcount", imgcounttourists);
            }
        } else {
            model.addAttribute("loginid", -2);
            model.addAttribute("imgcount", imgcounttourists);
            model.addAttribute("filesize", filesizetourists * 1024 * 1024);
        }
        model.addAttribute("suffix", uploadConfig.getSuffix());
        model.addAttribute("config", config);
        model.addAttribute("uploadConfig", uploadConfig);
        Integer isupdate = 1;
        if (uploadConfig.getIsupdate() != 1) {
            isupdate = (u == null) ? 0 : 1;//如果u等于null那么isupdate就等于0，否则等于1
        }
        model.addAttribute("ykxz", isupdate);
        return "index";

    }

    @RequestMapping("/upimg")
    @ResponseBody
    public String upimg(@RequestParam(value = "file", required = false) MultipartFile[] file, Integer setday,
                        HttpSession session) throws Exception {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        Config config = configService.getSourceype();
        UploadConfig uploadConfig = uploadConfigService.getUpdateConfig();
        User u = (User) session.getAttribute("user");
        Integer usermemory = 0;
        Integer memory = 0;
        Integer Sourcekey = 0;
        if (u == null) {
            Sourcekey = GetCurrentSource.GetSource(null);
            memory = uploadConfig.getVisitormemory();
            usermemory = imageService.getusermemory(0);
            if (usermemory == null) {
                usermemory = 0;
            }
        } else {
            Sourcekey = GetCurrentSource.GetSource(u.getId());
            memory = userService.getUsers(u.getEmail()).getMemory();
            usermemory = imageService.getusermemory(u.getId());
            if (usermemory == null) {
                usermemory = 0;
            }
        }
        Keys key = keysService.selectKeys(Sourcekey);
        Boolean b = false;
        if (Sourcekey == 5) {
            b = true;
        } else {
            b = StringUtils.doNull(Sourcekey, key);
        }

        if (!b) {
            jsonObject.put("imgurls", -1);
            return jsonObject.toString();
        }

        int tmp = usermemory / 1024;
        //证明未做限制
        if (memory == -1) {
            tmp = -2;
        }
        // 可用空间不足
        if (tmp >= memory) {
            jsonObject.put("imgurls", -5);
            return jsonObject.toString();
        }
        long stime = System.currentTimeMillis();
        String userpath = "tourist";
        if (uploadConfig.getUrltype() == 2) {
            java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd");
            userpath = dateFormat.format(new Date());
        } else {
            if (u != null) {
                userpath = u.getUsername();
            }
        }
        Map<String, MultipartFile> map = new HashMap<>();
        for (MultipartFile multipartFile : file) {
            // 获取ImageReader对象的迭代器
            String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
            if (!multipartFile.isEmpty()) {
                map.put(ext, multipartFile);
                System.out.println("===name===" + multipartFile.getOriginalFilename());  //文件名
            }
        }
        Map<ReturnImage, Integer> m = null;
        if (key.getStorageType() == 1) {
            m = nOSImageupload.Imageupload(map, userpath, null, setday);
        } else if (key.getStorageType() == 2) {
            m = ossImageupload.ImageuploadOSS(map, userpath, null, setday);
        } else if (key.getStorageType() == 3) {
            m = ussImageupload.ImageuploadUSS(map, userpath, null, setday);
        } else if (key.getStorageType() == 4) {
            m = kodoImageupload.ImageuploadKODO(map, userpath, null, setday);
        } else if (key.getStorageType() == 5) {
            m = LocUpdateImg.ImageuploadLOC(map, userpath, null, setday);
        } else if (key.getStorageType() == 6) {
            m = cosImageupload.ImageuploadCOS(map, userpath, null, setday);
        } else if (key.getStorageType() == 7) {
            m = ftpImageupload.ImageuploadFTP(map, userpath, null, setday);
        } else {
            System.err.println("未获取到对象存储参数，上传失败。");
        }
        Images img = new Images();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        // 上传图片的时间
        String times = df.format(new Date());

        for (Map.Entry<ReturnImage, Integer> entry : m.entrySet()) {
            if (key.getStorageType() == 5) {
                jsonObject.put("imgurls", this.getDomain() + "/links/" + entry.getKey().getImgurl());
                jsonObject.put("imgnames", entry.getKey().getImgname());
                if (config.getDomain() != null) {
                    img.setImgurl(config.getDomain() + "/links/" + entry.getKey().getImgurl());//图片链接
                } else {
                    img.setImgurl("http://" + IPPortUtil.getLocalIP() + ":" + IPPortUtil.getLocalPort() + "/links/" + entry.getKey().getImgurl());//图片链接
                }
            } else {
                jsonObject.put("imgurls", entry.getKey().getImgurl());
                jsonObject.put("imgnames", entry.getKey().getImgname());
                img.setImgurl(entry.getKey().getImgurl());//图片链接
            }
            jsonArray.add(jsonObject);
            img.setUpdatetime(times);
            img.setSource(key.getStorageType());
            if (u == null) {
                img.setUserid(0);
            } else {
                img.setUserid(u.getId());//用户id
            }
            img.setSizes((entry.getValue()) / 1024);
            img.setImgname(SetText.getSubString(entry.getKey().getImgurl(), key.getRequestAddress() + "/", ""));
            img.setAbnormal(0);
            userService.insertimg(img);
            long etime = System.currentTimeMillis();

            System.out.println("上传图片所用时长：" + String.valueOf(etime - stime) + "ms");
        }
        return jsonObject.toString();
    }


    /**
     * 根据网络图片url上传
     */
    @PostMapping("/upurlimg")
    @ResponseBody
    public String upurlimg(HttpSession session, String imgurl, HttpServletRequest request, Integer setday) throws Exception {
        Config config = configService.getSourceype();//查询当前系统使用的存储源类型。
        UploadConfig uploadConfig = uploadConfigService.getUpdateConfig();
        User u = (User) session.getAttribute("user");

        Integer usermemory = 0;
        Integer memory = 0;
        Integer Sourcekey = 0;
        if (u == null) {
            Sourcekey = GetCurrentSource.GetSource(null);
            memory = uploadConfig.getVisitormemory();
            usermemory = imageService.getusermemory(0);
            if (usermemory == null) {
                usermemory = 0;
            }
        } else {
            Sourcekey = GetCurrentSource.GetSource(u.getId());
            memory = userService.getUsers(u.getEmail()).getMemory();
            usermemory = imageService.getusermemory(u.getId());
            if (usermemory == null) {
                usermemory = 0;
            }
        }
        //Integer Sourcekey = GetCurrentSource.GetSource(u.getId());
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

        Keys key = keysService.selectKeys(Sourcekey);
        long imgsize = ImgUrlUtil.getFileLength(imgurl);
        Integer youke = uploadConfig.getFilesizetourists();
        Integer yonghu = uploadConfig.getFilesizeuser();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        Boolean bo = false;
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
            usermemory = imageService.getusermemory(0);
            if (usermemory == null) {
                usermemory = 0;
            }
        } else {
            memory = userService.getUsers(u.getEmail()).getMemory();
            usermemory = imageService.getusermemory(u.getId());
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
                                            img.setUserid(0);//用户id
                                        } else {
                                            img.setUserid(u.getId());//用户id
                                        }
                                        img.setSizes((entry.getValue()));
                                        img.setImgname(SetText.getSubString(entry.getKey().getImgurl(), key.getRequestAddress() + "/", ""));
                                        img.setAbnormal(0);
                                        userService.insertimg(img);
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
                                            img.setUserid(0);//用户id
                                        } else {
                                            img.setUserid(u.getId());//用户id
                                        }
                                        img.setSizes((entry.getValue()));
                                        img.setImgname(SetText.getSubString(entry.getKey().getImgurl(), key.getRequestAddress() + "/", ""));
                                        img.setAbnormal(0);
                                        userService.insertimg(img);
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


    private String getDomain() {
        String protocol = "1".equals(this.enableHttps) ? "https" : "http";
        String localAddr = this.request.getLocalAddr();
        localAddr = "0:0:0:0:0:0:0:1".equals(localAddr) ? "localhost" : localAddr;
        return protocol + "://" + localAddr + ":" + this.request.getLocalPort();
    }
}
