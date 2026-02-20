package com.example.demo.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtProvider {

  @Value("${jwt.secret}")
  private String salt;

  @Value("${jwt.access-expiration}")
  private long accessTokenExp;

  private SecretKey secretKey;
  
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
  
}
