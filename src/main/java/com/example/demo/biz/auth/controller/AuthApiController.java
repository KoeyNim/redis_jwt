package com.example.demo.biz.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.biz.auth.dto.AuthRequest;
import com.example.demo.biz.auth.service.AuthService;
import com.example.demo.common.dto.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

  private final AuthService service;

  @PostMapping("login")
  public ResponseEntity<ApiResponse<String>> login(@RequestBody AuthRequest req, HttpServletResponse httpRes) {
    return ResponseEntity.ok(ApiResponse.success(service.login(req, httpRes)));
  }

  @PostMapping("logout")
  public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse httpRes) {
    service.logout(userDetails.getUsername(), httpRes);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @PostMapping("refresh")
  public ResponseEntity<ApiResponse<String>> refresh(
      @AuthenticationPrincipal UserDetails userDetails,
      @CookieValue(name = "refreshToken", required = false) String refreshToken,
      HttpServletResponse httpRes) {
    return ResponseEntity.ok(ApiResponse.success(service.refresh(userDetails.getUsername(), refreshToken, httpRes)));
  }
}
