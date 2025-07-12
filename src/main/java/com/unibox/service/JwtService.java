package com.unibox.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private SecretKey secretKey;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final long EXPIRATION_MS = 86400000; // 1 day

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Unified token generator with optional departmentId and role
    public String generateToken(String email, String role, Long departmentId) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(secretKey, SignatureAlgorithm.HS256);

        if (departmentId != null) {
            builder.claim("departmentId", departmentId);
        }

        return builder.compact();
    }

    // Extract claims helper method
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = parseToken(token);
        return claims != null ? claimsResolver.apply(claims) : null;
    }

    // Parse token and return claims or null if invalid
    private Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null; // Invalid token
        }
    }

    // Extract email from request
    public String extractEmail(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        return extractClaim(token, Claims::getSubject);
    }

    // Extract role from request
    public String extractRole(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Extract departmentId from request (may be null)
    public Long extractDepartmentIdFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        Number deptId = extractClaim(token, claims -> claims.get("departmentId", Number.class));
        return deptId != null ? deptId.longValue() : null;
    }

    // Helper to extract token string from Authorization header
    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
