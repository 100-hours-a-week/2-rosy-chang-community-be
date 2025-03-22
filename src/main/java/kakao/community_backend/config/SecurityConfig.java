package kakao.community_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 람다 기반 구성 사용
        http
                // CSRF 비활성화
                .csrf(csrf -> csrf.disable())
                // 폼 로그인 비활성화
                .formLogin(formLogin -> formLogin.disable())
                // HTTP 기본 인증 비활성화
                .httpBasic(httpBasic -> httpBasic.disable())
                // 세션 관리 설정
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 요청 인증 설정
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/users/signup", "/users/login", "/refresh").permitAll()
                                .anyRequest().permitAll() // 개발 단계에서는 모든 요청 허용
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}