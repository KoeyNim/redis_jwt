package com.example.demo.biz.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.biz.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@Controller
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService service;

  @PostMapping("login")
  public String login(@RequestParam String username, @RequestParam String password) {
    return service.login(username, password);
  }
}
