package club._8b1t.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 生成用户token的工具类
 *
 * @author 8bit
 */
public class JwtUtil {

    // 使用Keys.secretKeyFor生成一个安全的密钥
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 生成JWT令牌
    public static String generateAccessToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
//        10小时过期
        return createToken(claims, 1000 * 60 * 60 * 10);
    }

    // 生成刷新令牌
    public static String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
//        7天过期
        return createToken(claims, 1000 * 60 * 60 * 24 * 7);
    }

    // 创建JWT令牌
    private static String createToken(Map<String, Object> claims, long expirationMillis) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // 验证JWT令牌
    public static Boolean validateToken(String token, Long userId) {
        final Long extractedUserId = extractUserId(token);
        return (extractedUserId.equals(userId) && !isTokenExpired(token));
    }

    // 提取用户ID
    public static Long extractUserId(String token) {
        return ((Number) extractAllClaims(token).get("id")).longValue();
    }

    // 提取过期时间
    public static Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 提取声明
    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 提取所有声明
    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 检查令牌是否过期
    private static Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
