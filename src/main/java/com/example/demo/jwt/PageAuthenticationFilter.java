package com.example.demo.jwt;

import java.io.IOException;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.example.demo.biz.member.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 페이지(UI) 전용 인증 필터
 * 쿠키의 refreshToken을 사용하여 사용자를 식별하고 SecurityContext를 채움
 */
@Component
@RequiredArgsConstructor
public class PageAuthenticationFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        if (path.startsWith("/api") // /api/** 요청은 JwtAuthenticationFilter가 처리하므로 무시
                || path.equals("/auth/login") // 로그인 페이지는 인증 불필요
                || PathRequest.toStaticResources().atCommonLocations().matches(request)) { // 정적 리소스 무시
            return true;
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        authenticateByCookie(request);

        // 이미 로그인된 사용자가 로그인 페이지 접근 시 메인으로 리다이렉트
        String path = request.getServletPath();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (path.equals("/auth/login") && auth != null && auth.isAuthenticated()
                && !auth.getPrincipal().equals("anonymousUser")) {
            response.sendRedirect("/");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateByCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "refreshToken");
        if (cookie != null) {
            String refreshTokenKey = cookie.getValue();
            // 해당 키가 유효한지 확인
            Boolean isValid = redisTemplate.hasKey(refreshTokenKey);

            if (isValid) {
                // username 추출
                String[] tokens = refreshTokenKey.split(":");
                if (tokens.length >= 2) {
                    String username = tokens[1];

                    // 인증 객체 생성
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                    Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "",
                            userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
    }
}
