// JwtAuthenticationInterceptor.java
package kakao.community_backend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kakao.community_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 요청은 통과
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 인증이 필요하지 않은 경로 패턴 정의
        boolean isPublicGetEndpoint =
                (method.equals("GET") && (requestURI.matches("/posts") ||
                        requestURI.matches("/posts/\\d+") ||
                        requestURI.matches("/posts/\\d+/comments")));

        if (isPublicGetEndpoint) {
            return true;  // 인증 없이 접근 가능
        }

        // 이하 기존 인증 코드
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\": 401, \"message\": \"인증에 실패했습니다\"}");
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\": 401, \"message\": \"인증에 실패했습니다\"}");
            return false;
        }

        // 요청 속성에 사용자 ID 저장
        Long userId = jwtUtil.extractUserId(token);
        request.setAttribute("userId", userId);
        return true;
    }
}