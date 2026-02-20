package com.example.demo.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.demo.biz.auth.service.CustomUserDetailsService;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {

  @Value("${jwt.secret}")
  private String salt;

  @Value("${jwt.access-expiration}")
  private long accessTokenExp;

  private SecretKey secretKey;
  
  private final CustomUserDetailsService customUserDetailsService;
  
  @PostConstruct
  protected void init() {
    this.secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
  }

  // access token 생성
  public String createAccessToken(String username) {
    Date now = new Date();
    Date exp = new Date(now.getTime() + accessTokenExp); // 만료시간
    return Jwts.builder()
        .subject(username)
        .issuedAt(now)
        .expiration(exp)
        .signWith(secretKey)
        .compact();
  }

  public SecretKey getSecretKey() {
    return this.secretKey;
  }

  public String getUserPk(String token) {
    return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
  }

  public Authentication getAuthentication(String token) {
      UserDetails userDetails = customUserDetailsService.loadUserByUsername(this.getUserPk(token));
      return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  // 토큰 검증
  public boolean validateToken(String jwtToken) {
    try {
        Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(jwtToken);
        return true;
    } catch (JwtException | IllegalArgumentException e) {
        return false;
    }
  }
}
