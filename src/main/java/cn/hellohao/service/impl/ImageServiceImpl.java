package cn.hellohao.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.hellohao.config.SystemConfig;
import cn.hellohao.pojo.Image;
import cn.hellohao.utils.Print;
import com.UpYun;
import com.aliyun.oss.OSSClient;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.region.Region;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import com.upyun.UpException;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.netease.cloud.auth.BasicCredentials;
import com.netease.cloud.auth.Credentials;
import com.netease.cloud.services.nos.NosClient;
import com.netease.cloud.services.nos.model.Bucket;
import com.netease.cloud.services.nos.model.CannedAccessControlList;
import com.netease.cloud.services.nos.transfer.TransferManager;

import cn.hellohao.dao.ImageMapper;
import cn.hellohao.pojo.Images;
import cn.hellohao.pojo.Keys;
import cn.hellohao.service.ImageService;

/**
 * @author su
 * @date 2019/10/19 13:26
 */
@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private SystemConfig systemConfig;

    @Override
    public Integer insert(Image image) {
        return this.imageMapper.insert(image);
    }

    @Override
    public List<Images> selectimg(Images images) {
        // TODO Auto-generated method stub
        return imageMapper.selectimg(images);
    }

    @Override
    public Integer delete(Long id) {
        return imageMapper.delete(id);
    }


    public Images selectByPrimaryKey(Integer id) {
        return imageMapper.selectByPrimaryKey(id);
    }

    //删除对象存储的图片文件
    public void delect(Keys key, String fileName) {
        // 初始化
        Credentials credentials = new BasicCredentials(key.getAccessKey(), key.getAccessSecret());
        NosClient nosClient = new NosClient(credentials);
        nosClient.setEndpoint(key.getEndpoint());
        // 初始化TransferManager
        TransferManager transferManager = new TransferManager(nosClient);
        //列举桶
        ArrayList bucketList = new ArrayList();
        String tname = "";
        for (Bucket bucket : nosClient.listBuckets()) {
            bucketList.add(bucket.getName());
        }
        for (Object object : bucketList) {
            tname = object.toString();
            //查看桶的ACL
            CannedAccessControlList acl = nosClient.getBucketAcl(object.toString());
            // bucket权限
        }
        //这种方法不能删除指定文件夹下的文件
        boolean isExist = nosClient.doesObjectExist(tname, fileName, null);
        System.out.println("文件是否存在：" + isExist);
        if (isExist) {
            nosClient.deleteObject(tname, fileName);
        }
    }

    //删除OSS对象存储的图片文件
    public void delectOSS(Keys key, String fileName) {
        // 初始化
// Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = key.getEndpoint();
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = key.getAccessKey();
        String accessKeySecret = key.getAccessSecret();
        String bucketName = key.getBucketname();
        String objectName = fileName;
// 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
// 删除文件。
        ossClient.deleteObject(bucketName, objectName);
// 关闭OSSClient。
        ossClient.shutdown();
    }

    //删除USS对象存储的图片文件
    public void delectUSS(Keys key, String fileName) {
        UpYun upyun = new UpYun(key.getBucketname(), key.getAccessKey(), key.getAccessSecret());
        try {
            boolean result = upyun.deleteFile(fileName, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UpException e) {
            e.printStackTrace();
        }
    }

    //删除KODO对象存储的图片文件
    public void delectKODO(Keys key, String fileName) {
        Configuration cfg;
        //构造一个带指定Zone对象的配置类
        if (key.getEndpoint().equals("1")) {
            cfg = new Configuration(Zone.zone0());
        } else if (key.getEndpoint().equals("2")) {
            cfg = new Configuration(Zone.zone1());
        } else if (key.getEndpoint().equals("3")) {
            cfg = new Configuration(Zone.zone2());
        } else if (key.getEndpoint().equals("4")) {
            cfg = new Configuration(Zone.zoneNa0());
        } else {
            cfg = new Configuration(Zone.zoneAs0());
        }
        Auth auth = Auth.create(key.getAccessKey(), key.getAccessSecret());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(key.getBucketname(), fileName);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }

    //删除COS对象存储的图片文件
    public void delectCOS(Keys key, String fileName) {
        COSCredentials cred = new BasicCOSCredentials(key.getAccessKey(), key.getAccessSecret());
        Region region = new Region(key.getEndpoint());
        ClientConfig clientConfig = new ClientConfig(region);
        COSClient cosClient = new COSClient(cred, clientConfig);
        try {
            // 指定对象所在的存储桶
            String bucketName = key.getBucketname();
            // 指定对象在 COS 上的对象键
            String userkey = fileName;
            cosClient.deleteObject(key.getBucketname(), userkey);
        } catch (CosServiceException serverException) {
            serverException.printStackTrace();
        } catch (CosClientException clientException) {
            clientException.printStackTrace();
        }
    }

    //删除FTP存储的图片文件
    public void delectFTP(Keys key, String fileName) {
        FTPClient ftp = new FTPClient();
        String[] host = key.getEndpoint().split("\\:");
        String h = host[0];
        Integer p = Integer.parseInt(host[1]);
        try {
            if (!ftp.isConnected()) {
                ftp.connect(h, p);
            }
            //如果是需要认证的服务器，就需要账号和密码来登录
            ftp.login(key.getAccessKey(), key.getAccessSecret());
            ftp.deleteFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Print.warning("删除FTP存储的图片失败");
        }
    }

    @Override
    public Integer counts(Integer userid) {
        // TODO Auto-generated method stub
        return imageMapper.counts(userid);
    }


    @Override
    public Integer countimg(Integer userid) {
        // TODO Auto-generated method stub
        return imageMapper.countimg(userid);
    }

    @Override
    public Integer setabnormal(String imgname) {
        return imageMapper.setabnormal(imgname);
    }

    @Override
    public Integer deleimgname(String imgname) {
        return imageMapper.deleimgname(imgname);
    }

    @Override
    public Integer deleall(Integer id) {
        return imageMapper.deleall(id);
    }

    @Override
    public List<Images> gettimeimg(String time) {
        return imageMapper.gettimeimg(time);
    }

    @Override
    public Integer getusermemory(Integer userid) {
        return imageMapper.getusermemory(userid);
    }

    @Override
    public String getImagePath(long imageId) {
        return this.getSavePath() + imageId;
    }

    @Override
    public String getSavePath() {
        String osType = System.getProperty("os.name");
        return osType.toLowerCase().contains("window") ? this.systemConfig.imageSavePath4windows : this.systemConfig.imageSavePath4linux;
    }
}
