package com.example.demo.biz.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.biz.auth.dto.AuthRequest;
import com.example.demo.biz.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@Controller
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

  private final AuthService service;

  @PostMapping("login")
  public String login(@RequestBody AuthRequest req) {
    return service.login(req);
  }
}
