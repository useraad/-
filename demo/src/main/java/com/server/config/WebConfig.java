package com.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);
    }

    // 🔥 新增：配置静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 意思：访问 http://localhost:8080/images/xxx.jpg -> D:/waimai/images/xxx.jpg
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:D:/waimai/images/");
    }
}