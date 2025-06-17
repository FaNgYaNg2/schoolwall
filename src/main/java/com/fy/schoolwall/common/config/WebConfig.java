package com.fy.schoolwall.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 
 * 意义：
 * 1. 配置跨域资源共享(CORS)，允许前端应用访问API
 * 2. 设置统一的Web层配置，如拦截器、转换器等
 * 3. 提供灵活的配置选项，便于不同环境的部署
 * 4. 确保安全性，只允许特定域名的跨域访问
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置跨域资源共享(CORS)
     * 
     * 开发环境通常需要允许localhost的跨域访问
     * 生产环境需要限制为特定的域名
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 只对API路径生效
                .allowedOriginPatterns("*") // 开发环境允许所有域名，生产环境需要具体配置
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的HTTP方法
                .allowedHeaders("*") // 允许的请求头
                .allowCredentials(true) // 允许发送Cookie
                .maxAge(3600); // 预检请求的缓存时间（秒）

        // 为管理员API单独配置CORS（可选）
        registry.addMapping("/admin/**")
                .allowedOriginPatterns("*") // 生产环境应该限制为管理后台域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        // 为用户API配置CORS
        registry.addMapping("/users/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 可以在这里添加其他Web配置
     * 如：拦截器、消息转换器、视图解析器等
     */

    // 示例：添加拦截器配置
    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
    // registry.addInterceptor(new LoggingInterceptor())
    // .addPathPatterns("/**")
    // .excludePathPatterns("/static/**");
    // }
}