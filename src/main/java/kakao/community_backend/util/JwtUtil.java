// JwtUtil.java
package kakao.community_backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret:defaultSecretKey}")
    private String SECRET_KEY;

    @Value("${jwt.expiration:86400000}")
    private long JWT_TOKEN_VALIDITY;

    @Value("${jwt.refresh-expiration:604800000}")
    private long REFRESH_TOKEN_VALIDITY;

    // 토큰에서 사용자 ID 추출
    public Long extractUserId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }

    // 토큰에서 만료 시간 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 토큰에서 클레임 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 모든 클레임 추출
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰 만료 여부 확인
    private Boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    // 액세스 토큰 생성
    public String generateToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId.toString(), JWT_TOKEN_VALIDITY);
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId.toString(), REFRESH_TOKEN_VALIDITY);
    }

    // 토큰 생성
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 검증
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    // 서명 키 생성
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}