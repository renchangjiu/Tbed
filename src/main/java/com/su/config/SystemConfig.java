package com.su.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author su
 * @date 2019/10/20 13:17
 */
@Component
public class SystemConfig {

    @Value("${system.image.save.path.windows}")
    public String imageSavePath4windows;

    @Value("${system.image.save.path.linux}")
    public String imageSavePath4linux;

    @Value("${system.image.save-domain}")
    public String imageSaveDomain;

    @Value("${system.image.tourist-total-memory}")
    public Integer touristTotalMemory;

    @Value("${system.image.user-default-memory}")
    public Integer userDefaultMemory;

    @Value("${system.image.tourist-uploadable}")
    private String q;
    private boolean touristUploadable;
    public boolean getTouristUploadable() {
        return StringUtils.isEmpty(this.q) || "1".equals(this.q);
    }

    @Value("${system.image.tourist-once-upload-max-size}")
    public Integer touristOnceUploadMaxSize;

    @Value("${system.image.user-once-upload-max-size}")
    public Integer userOnceUploadMaxSize;

    @Value("${system.enable-email-verification}")
    public Integer enableEmailVerification;

    @Value("${system.enable-register}")
    private String q1;
    public boolean getEnableRegister() {
        return StringUtils.isEmpty(q1) || "1".equals(q1);
    }
    /**
     * 返回本地存储中的保存图片的路径
     *
     * @return 图片的保存路径
     */
    public String getSavePath() {
        String osType = System.getProperty("os.name");
        return osType.toLowerCase().contains("window") ? this.imageSavePath4windows : this.imageSavePath4linux;
    }
}
