package com.wyk.student.config;

import com.wyk.student.interceptor.StudentInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private StudentInterceptor studentInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(studentInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/doc.html",                    // 文档首页
                        "/webjars/**",                  // 文档依赖的静态资源
                        "/v3/api-docs/**",              // 接口文档数据
                        "/swagger-resources/**",        // Swagger资源
                        "/swagger-ui/**",               // Swagger UI（兼容旧版本）
                        "/api/user/post",
                        "/api/user/record");
    }
}
