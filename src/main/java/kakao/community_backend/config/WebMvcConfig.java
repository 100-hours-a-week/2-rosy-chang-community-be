// WebMvcConfig.java
package kakao.community_backend.config;

import kakao.community_backend.interceptor.JwtAuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/users/profile", "/users/password", "/user",
                        "/posts/*/like", "/comments/**",
                        "/posts", // 게시글 작성 경로
                        "/posts/*/comments") // 댓글 작성 경로
                .excludePathPatterns("/users/signup", "/users/login", "/refresh",
                        "/posts/*/comments/list", // 댓글 목록 조회만 제외 (있다면)
                        "/posts/list", "/posts/view/*"); // 게시글 목록 및 조회만 제외
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:5500") // Live Server의 기본 포트
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}