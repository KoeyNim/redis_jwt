package com.example.demo.biz.auth.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.biz.member.CustomUserDetailsService;
import com.example.demo.jwt.JwtProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

  // RedisConfig.java Bean 주입
  private final RedisTemplate<String, Object> redisTemplate;

  private final CustomUserDetailsService customUserDetailsService;

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  private final JwtProvider jwtProvider;

  @Value("${jwt.refresh-expiration}")
  private long refreshExp;

  public String login(String username, String password) {
    // 임시 우회 인증
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
    if (!bCryptPasswordEncoder.matches(password, userDetails.getPassword())) {
      throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
    }

    String accessToken = jwtProvider.createAccessToken(username);
    String refreshToken = "RT-" + UUID.randomUUID().toString().replaceAll("-", "");

    // refreshToken 저장
    redisTemplate.opsForValue().set(
        "RT:" + username,
        refreshToken,
        refreshExp, TimeUnit.MILLISECONDS);
    return accessToken;
  }
}
