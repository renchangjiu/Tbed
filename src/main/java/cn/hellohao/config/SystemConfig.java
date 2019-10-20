package cn.hellohao.config;

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
}
