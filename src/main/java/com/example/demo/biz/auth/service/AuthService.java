package com.example.demo.biz.auth.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.biz.auth.dto.AuthRequest;
import com.example.demo.biz.auth.dto.RefreshRequest;
import com.example.demo.biz.auth.dto.TokenResponse;
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

  public TokenResponse login(AuthRequest req) {
    // 임시 우회 인증
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(req.getUsername());
    if (!bCryptPasswordEncoder.matches(req.getPassword(), userDetails.getPassword())) {
      throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
    }

    String accessToken = jwtProvider.createAccessToken(req.getUsername());
    String refreshToken = "RT-" + UUID.randomUUID().toString().replaceAll("-", "");

    // refreshToken 저장
    redisTemplate.opsForValue().set(
        "RT:" + req.getUsername(),
        refreshToken,
        refreshExp, TimeUnit.MILLISECONDS);
        
    return TokenResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  public TokenResponse refresh(RefreshRequest req) {
    // 1. Redis에서 사용자의 저장된 Refresh Token 조회
    String redisKey = "RT:" + req.getUsername();
    String savedRefreshToken = (String) redisTemplate.opsForValue().get(redisKey);

    // 2. 검증: 만료되었거나 일치하지 않으면 예외 발생
    if (savedRefreshToken == null || !savedRefreshToken.equals(req.getRefreshToken())) {
      throw new RuntimeException("Refresh Token이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요.");
    }

    // 3. 새로운 Access Token 발급
    String newAccessToken = jwtProvider.createAccessToken(req.getUsername());

    // 4. (선택적) Refresh Token Rotation - 기존걸 지우고 새로 발급할 수도 있음 (여기선 유지)
    
    return TokenResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(req.getRefreshToken()) // 기존거 그대로 반환 (연장)
        .build();
  }
}
