package cn.hellohao.config;

import cn.hellohao.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author su
 * @date 2019/10/19 12:07
 */
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    private ImageService imageService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String filePath = this.imageService.getSavePath();
        // 映射到本地路径
        registry.addResourceHandler("/links/**").addResourceLocations("file:" + filePath);
    }
}
