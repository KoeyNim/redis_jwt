package com.example.demo.biz.auth.dto;

import lombok.Data;

@Data
public class AuthRequest {
  private final String username;
  private final String password;
}
