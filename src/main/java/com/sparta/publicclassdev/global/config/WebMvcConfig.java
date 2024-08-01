package com.sparta.publicclassdev.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("http://localhost:3000/") // “*“같은 와일드카드를 사용
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH") // 허용할 HTTP method
            .allowCredentials(true); // 쿠키 인증 요청 허용
    }
}
