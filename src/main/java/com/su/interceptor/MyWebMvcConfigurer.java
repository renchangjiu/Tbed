package com.su.interceptor;

import com.su.config.SystemConfig;
import com.su.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author su
 * @date 2019/10/20 13:37
 */
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {
    private final LoginInterceptor loginInterceptor;

    private final InterceptorConfigTwo interceptorConfigTwo;


    private final SystemConfig systemConfig;

    public MyWebMvcConfigurer(LoginInterceptor loginInterceptor, InterceptorConfigTwo interceptorConfigTwo, SystemConfig systemConfig) {
        this.loginInterceptor = loginInterceptor;
        this.interceptorConfigTwo = interceptorConfigTwo;
        this.systemConfig = systemConfig;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String filePath = this.systemConfig.getSavePath();
        // 映射到本地路径
        registry.addResourceHandler("/links/**").addResourceLocations("file:" + filePath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/admin/**");
        registry.addInterceptor(interceptorConfigTwo).addPathPatterns("/admin/root/**");
    }

}
