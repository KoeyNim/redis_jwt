package com.example.demo.biz.auth.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.biz.auth.dto.AuthRequest;
import com.example.demo.biz.member.CustomUserDetailsService;
import com.example.demo.jwt.JwtProvider;

import jakarta.servlet.http.HttpServletResponse;
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

  public String login(AuthRequest req, HttpServletResponse httpRes) {
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

    setRefreshTokenCookie(httpRes, refreshToken, refreshExp / 1000);
    return accessToken;
  }

  public void logout(String username, HttpServletResponse httpRes) {
    // Redis에서 해당 사용자의 Refresh Token 삭제
    redisTemplate.delete("RT:" + username);
    // Refresh Token 쿠키 삭제
    setRefreshTokenCookie(httpRes, null, 0);
  }

  public String refresh(String username, String refreshToken, HttpServletResponse httpRes) {
    // Redis에서 사용자의 저장된 Refresh Token 조회
    String refreshTokenInRedis = (String) redisTemplate.opsForValue().get("RT:" + username);
    // 검증: 만료되었거나 일치하지 않으면 예외 발생
    if (refreshTokenInRedis == null || !refreshTokenInRedis.equals(refreshToken)) {
      throw new RuntimeException("Refresh Token이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요.");
    }
    // 새로운 Access Token 발급
    return jwtProvider.createAccessToken(username);
  }

  private void setRefreshTokenCookie(HttpServletResponse httpRes, String token, long maxAge) {
    ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
        .httpOnly(true)
        .secure(false) // 개발 환경이므로 false, 운영은 true 권장 (SSL 인증서(HTTPS) 적용시 true)
        .path("/api") // 쿠키가 전송될 유효한 URL 경로
        .maxAge(maxAge)
        .sameSite("Lax")
        .build();
    httpRes.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
