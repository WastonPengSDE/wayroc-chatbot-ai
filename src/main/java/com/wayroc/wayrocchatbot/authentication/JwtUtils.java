package com.wayroc.wayrocchatbot.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具：生成与解析 token，用于登录鉴权。
 * 配置见 application.yml：jwt.secret、jwt.expire-seconds。
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire-seconds}")
    private long expireSeconds;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireSeconds * 1000L);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * @param token 请求头中的 token（可带或不带 "Bearer " 前缀）
     * @return 用户 ID，解析失败返回 null
     */
    public Long getUserIdFromToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String trimmed = token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            Claims payload = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(trimmed)
                    .getPayload();
            String subject = payload.getSubject();
            return Long.parseLong(subject);
        } catch ( JwtException | NumberFormatException e) {
            return null;
        }
    }
}
