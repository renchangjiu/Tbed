package com.su.service.impl;

import com.su.dao.KeysMapper;
import com.su.pojo.Keys;
import com.su.utils.Print;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Hellohao
 * @version 1.0
 * @date 2019/8/28 15:33
 */

@Component
@Order(2)   //通过order值的大小来决定启动的顺序
public class InitializationStorage implements CommandLineRunner {
    @Autowired
    private KeysMapper keysMapper;

    @Override
    public void run(String... args) throws Exception {
        intiStorage();
        sout();
    }
    public void intiStorage(){
        List<Keys> keylist = keysMapper.getKeys();
        for (Keys key : keylist) {
            if(key.getStorageType()!=0 && key.getStorageType()!=null){
                int ret =0;
                if(key.getStorageType()==1){
                    ret =NOSImageupload.Initialize(key);//实例化网易
                }else if (key.getStorageType()==2){
                    ret =OSSImageupload.Initialize(key);
                }else if(key.getStorageType()==3){
                    ret = USSImageupload.Initialize(key);
                }else if(key.getStorageType()==4){
                    ret = KODOImageupload.Initialize(key);
                }else if(key.getStorageType()==6){
                    ret = COSImageupload.Initialize(key);
                }else if(key.getStorageType()==7){
                    ret = FTPImageupload.Initialize(key);
                }
                else{
                    //Print.warning("未获取到存储参数，或者使用存储源是本地的。");
                }
//                if(ret<1){model.addAttribute("error", "当前存储源参数配置不完整，请联系管理员配置存储源。");}
//                else{model.addAttribute("error", 1);}
            }
        }
Print.Normal("初始化完成");
    }

    public void sout(){
        Print.Normal("______________________________________________");
        Print.Normal("              Hellohao-Pro                ");
        Print.Normal("     Successful startup of the program      ");
        Print.Normal("     is OK!  Open http://your ip:port       ");
        Print.Normal("______________________________________________");
    }
}
