package com.example.demo.biz.auth.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

  // RedisConfig.java Bean 주입
  private final RedisTemplate<String, Object> redisTemplate;

  private final JwtProvider jwtProvider;
  
  @Value("${jwt.refresh-expiration}")
  private long refreshExp;

  public String login(String username, String password) {
    if ("myuser".equals(username) && "123123".equals(password)) {
      String accessToken = jwtProvider.createAccessToken(username);
      String refreshToken = "RT-" + UUID.randomUUID().toString().replaceAll("-", "");

      // refreshToken 저장
      redisTemplate.opsForValue().set(
          "RT:" + username, 
          refreshToken, 
          refreshExp, TimeUnit.MILLISECONDS
      );
      return accessToken;
    }
    throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
  }

}
