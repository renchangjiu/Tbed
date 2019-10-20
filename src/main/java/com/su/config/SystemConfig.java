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

    @Value("${system.image.tourist-uploadable}")
    private String q;
    public boolean touristUploadable = StringUtils.isEmpty(this.q) || "1".equals(this.q);

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
