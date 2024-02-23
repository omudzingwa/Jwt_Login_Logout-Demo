package com.quad.login_logout.jwtutils;

import com.quad.login_logout.userdetails.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtTokenProvider {
    private final RedisTemplate<String, String> redisTemplate;
    private final UserDetailsServiceImpl userDetailsService;

    //@Value("${spring.jwt.secret}")
    //private String secretKey;

    @Value("${spring.jwt.access-token-expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${spring.jwt.refresh-token-expiration-time}")
    private long refreshTokenExpirationTime;

    SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     *
     Generate Access Token
     */
    public String createAccessToken(Authentication authentication){
        Claims claims = (Claims) Jwts.claims().subject(authentication.getName());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessTokenExpirationTime);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     *
     * Generate Refresh Token
     */
    public String createRefreshToken(Authentication authentication){
        Claims claims = (Claims) Jwts.claims().subject(authentication.getName());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshTokenExpirationTime);

        String refreshToken = Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();

        // Save to Redis
        redisTemplate.opsForValue().set(
                authentication.getName(),
                refreshToken,
                refreshTokenExpirationTime,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    /**
     * Create a claim from the token, create a User object through it, and return an Authentication object
     */
    public Authentication getAuthentication(String token) {
        String userPrincipal = Jwts.parser().
                verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * Retrieves the bearer token from the http header.
     */
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Verify Access Token
     */
    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }
}
