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
    String username = req.getUsername();
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
    if (!bCryptPasswordEncoder.matches(req.getPassword(), userDetails.getPassword())) {
      throw new RuntimeException("아이디 또는 비밀번호가 틀렸습니다.");
    }

    String accessToken = jwtProvider.createAccessToken(username);
    String refreshToken = jwtProvider.createRefreshToken(username);

    // RT:username:uuid 형식으로 저장
    String redisKey = "RT:" + username + ":" + UUID.randomUUID().toString().replaceAll("-", "");
    redisTemplate.opsForValue().set(
        redisKey,
        refreshToken, // 값은 검증용으로 토큰 문자열 저장
        refreshExp,
        TimeUnit.MILLISECONDS);

    setRefreshTokenCookie(httpRes, redisKey, refreshExp / 1000);

    return accessToken;
  }

  public void logout(String refreshTokenKey, HttpServletResponse httpRes) {
    // Redis에서 해당 리프레시 토큰 키 삭제
    redisTemplate.delete(refreshTokenKey);

    // Refresh Token 쿠키 삭제
    setRefreshTokenCookie(httpRes, null, 0);
  }

  public String refresh(String refreshTokenKey, HttpServletResponse httpRes) {
    // Redis에서 해당 키(RT:username:uuid)가 유효한지 조회
    String savedToken = (String) redisTemplate.opsForValue().get(refreshTokenKey);

    if (savedToken == null) {
      throw new RuntimeException("Refresh Token이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요.");
    }

    // 키 형식(RT:username:uuid)에서 username 추출
    String username = refreshTokenKey.split(":")[1];

    // 새로운 Access Token 발급
    return jwtProvider.createAccessToken(username);
  }

  private void setRefreshTokenCookie(HttpServletResponse httpRes, String token, long maxAge) {
    ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
        .httpOnly(true)
        .secure(false) // 개발 환경이므로 false, 운영은 true 권장 (SSL 인증서(HTTPS) 적용시 true)
        .path("/") // 모든 경로에서 쿠키를 볼 수 있도록 "/"
        .maxAge(maxAge)
        .sameSite("Lax")
        .build();
    httpRes.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
