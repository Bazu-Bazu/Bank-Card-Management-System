package com.example.bankcards.security.jwt;

import com.example.bankcards.security.userDetails.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Getter
public class JwtService {

    @Value("${jwt.secret-key}")
    private String jwtSecret;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        claims.put("authorities", authorities);
        claims.put("token_type", "ACCESS");

        addRoleSpecificId(claims, userDetails);

        Date date = createExpirationDate(accessTokenExpiration);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "REFRESH");

        Date date = createExpirationDate(refreshTokenExpiration);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    public Collection<? extends GrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);

        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");

        if (authorities == null) {
            return List.of();
        }

        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isRefreshToken(String token) {
        return "REFRESH".equals(
                extractClaim(token, c -> c.get("token_type", String.class))
        );
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private void addRoleSpecificId(Map<String, Object> claims, UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails customUserDetails) {
            claims.put("user_id", customUserDetails.getUserId().toString());
            claims.put("role", customUserDetails.getRole());
        }
    }

    private Date createExpirationDate(long expirationMillis) {
        return Date.from(
                Instant.now()
                        .plusMillis(expirationMillis)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

}
