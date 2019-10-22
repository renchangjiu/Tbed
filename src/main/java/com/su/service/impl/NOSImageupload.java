package com.su.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.su.pojo.Key;
import com.su.pojo.ReturnImage;
import com.su.pojo.UploadConfig;
import com.su.utils.DateUtils;
import com.su.utils.DeleImg;
import com.su.utils.BinUtils;
import com.netease.cloud.services.nos.model.ObjectMetadata;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;

@Service
public class NOSImageupload {
    //直接读取properties文件的值
//	@Value("${AccessKey}")
//	private String AccessKey;
//	@Value("${AccessSecret}")
//	private String AccessSecret;
//	@Value("${Endpoint}")
//	private String Endpoint;
//	@Value("${Bucketname}")
//	private String Bucketname;
//	@Value("${RequestAddress}")
//	private String RequestAddress;
    static String BarrelName;
    static NosClient nosClient;
    static Key key;

    public Map<ReturnImage, Integer> Imageupload(Map<String, MultipartFile> fileMap, String username,
                                                 Map<String, String> fileMap2, Integer setday) throws Exception {
        // 要上传文件的路径
        if(fileMap2==null){
            File file = null;
            Map<ReturnImage, Integer> ImgUrl = new HashMap<>();
            for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
                String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase().substring(0,5);//生成一个没有-的uuid，然后取前5位
                java.text.DateFormat format1 = new java.text.SimpleDateFormat("MMddhhmmss");
                String times = format1.format(new Date());
                file = changeFile(entry.getValue());
                nosClient.putObject(BarrelName, username + "/" + uuid+times + "." + entry.getKey(), file);
                ReturnImage returnImage = new ReturnImage();
                returnImage.setImgname(entry.getValue().getOriginalFilename());
                returnImage.setImgurl(key.getRequestAddress() + "/" + username + "/" + uuid+times + "." + entry.getKey());
                ImgUrl.put(returnImage, (int) (entry.getValue().getSize()));
                if(setday>0){
                    String deleimg = DateUtils.plusDay(setday);
                    DeleImg.charu(username + "/" + uuid+times + "." + entry.getKey()+"-"+deleimg+"-"+"1");
                }
            }
            return ImgUrl;
        }else{
            Map<ReturnImage, Integer> ImgUrl = new HashMap<>();
            //设置Header
            ObjectMetadata meta = new ObjectMetadata();
            meta.setHeader("Content-Disposition", "inline");
            for (Map.Entry<String, String> entry : fileMap2.entrySet()) {
                String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase().substring(0,5);//生成一个没有-的uuid，然后取前5位
                java.text.DateFormat format1 = new java.text.SimpleDateFormat("MMddhhmmss");
                String times = format1.format(new Date());
                String imgurl = entry.getValue();

                String head = "";
                if(entry.getKey().equals("jpg")||entry.getKey().equals("jpeg")){
                    head = "image/jpeg";
                }else if(entry.getKey().equals("png")){
                    head = "image/png";
                }else if(entry.getKey().equals("bmp")){
                    head = "image/bmp";
                }else if(entry.getKey().equals("gif")){
                    head = "image/gif";
                }else{
                    System.err.println("未知格式文件，无法定义header头。");
                }
                meta.setHeader("Content-Type", head);//image/jpeg
                File file = new File(imgurl);
                FileInputStream fileInputStream = new FileInputStream(file);
                try {
                    //nosClient.putObject(BarrelName, username + "/" + uuid+times + "." + entry.getKey(), new File(imgurl));
                    //new FileInputStream(file)
                    nosClient.putObject(BarrelName, username + "/" + uuid+times + "." + entry.getKey(), fileInputStream,meta);

                    ReturnImage returnImage = new ReturnImage();
                    returnImage.setImgurl(key.getRequestAddress() + "/" + username + "/" + uuid+times + "." + entry.getKey());
                    ImgUrl.put(returnImage, BinUtils.getFileSize2(new File(imgurl)));
                    if(setday>0) {
                        String deleimg = DateUtils.plusDay(setday);
                        DeleImg.charu(username + "/" + uuid + times + "." + entry.getKey() + "-" + deleimg + "-" + "1");
                    }

                    boolean bb= new File(imgurl).getAbsoluteFile().delete();
                } catch (Exception e) {
                    System.out.println("上传报错:" + e.getMessage());
                }
                if(fileInputStream!=null){
                    fileInputStream.close();
                }
            }
            return ImgUrl;
        }

    }

    // 转换文件方法
    private File changeFile(MultipartFile multipartFile) throws Exception {
        // 获取文件名
        String fileName = multipartFile.getOriginalFilename();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        // todo 修改临时文件文件名
        File file = File.createTempFile(fileName, prefix);
        // MultipartFile to File
        multipartFile.transferTo(file);
        return file;
    }


    //初始化网易NOS对象存储
    public static Integer Initialize(Key k) {
        int ret = -1;
        if(k.getEndpoint()!=null && k.getAccessSecret()!=null && k.getEndpoint()!=null
                && k.getBucketname()!=null && k.getRequestAddress()!=null ){
            if(!k.getEndpoint().equals("") && !k.getAccessSecret().equals("") && !k.getEndpoint().equals("")
                    && !k.getBucketname().equals("") && !k.getRequestAddress().equals("") ){
                // 初始化
                Credentials credentials = new BasicCredentials(k.getAccessKey(), k.getAccessSecret());
                nosClient = new NosClient(credentials);
                nosClient.setEndpoint(k.getEndpoint());
                // 列举桶
                ArrayList bucketList = new ArrayList();
    //加入传入的key值不正确，这里会报异常，页面500，如何捕捉异常处理。
                BarrelName = k.getBucketname();
    //            for (Bucket bucket : nosClient.listBuckets()) {
    //                bucketList.add(bucket.getName());
    //            }

    //            for (Object object : bucketList) {
    //                if (object.toString().equals(k.getBucketname())) {
    //                    BarrelName = object.toString();
    //                }
    //            }
                key = k;
                ret = 1;
            }else{
                ret = -1;
            }
        }else{
            ret = -1;
            //throw new StorageSourceInitException("当前数据源配置不完整，请管理员前往后台配置。");
        }
        return ret;
    }

    /**
     * 客户端接口
     * */
    public Map<ReturnImage, Integer> clientuploadNOS(Map<String, MultipartFile> fileMap, String username, UploadConfig uploadConfig) throws Exception {
        File file = null;
        Map<ReturnImage, Integer> ImgUrl = new HashMap<>();
        for (Map.Entry<String, MultipartFile> entry : fileMap.entrySet()) {
            String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase().substring(0,5);//生成一个没有-的uuid，然后取前5位
            java.text.DateFormat format1 = new java.text.SimpleDateFormat("MMddhhmmss");
            String times = format1.format(new Date());
            file = changeFile(entry.getValue());
            try {
                ReturnImage returnImage = new ReturnImage();
                if(entry.getValue().getSize()/1024<=uploadConfig.getFilesizeuser()*1024){
                    nosClient.putObject(BarrelName, username + "/" + uuid+times + "." + entry.getKey(), file);
                    //ImgUrl.put(key.getRequestAddress() + "/" + username + "/" + uuid+times + "." + entry.getKey(), (int) (entry.getValue().getSize()));
                    returnImage.setImgname(entry.getValue().getOriginalFilename());
                    returnImage.setImgurl(key.getRequestAddress() + "/" + username + "/" + uuid+times + "." + entry.getKey());
                    ImgUrl.put(returnImage, (int) (entry.getValue().getSize()));
                }else{
                    returnImage.setImgname(entry.getValue().getOriginalFilename());
                    returnImage.setImgurl("文件超出系统设定大小，不得超过");
                    ImgUrl.put(returnImage, -1);
                }
            } catch (Exception e) {
                System.out.println("上传报错:" + e.getMessage());
            }
        }
        return ImgUrl;
    }

}
