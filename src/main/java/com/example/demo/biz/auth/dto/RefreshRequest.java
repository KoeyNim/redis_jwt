package com.example.demo.biz.auth.dto;

import lombok.Data;

@Data
public class RefreshRequest {
    private String username;
    private String refreshToken;
}
