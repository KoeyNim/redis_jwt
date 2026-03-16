package com.example.demo.biz.board.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.biz.board.dto.BoardResponse;
import com.example.demo.biz.board.service.BoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/board")
@RequiredArgsConstructor
public class BoardController {

  private final BoardService service;

  @GetMapping
  public String get() {
    return "get ok";
  }

  @GetMapping("list")
  public List<BoardResponse> gets(Authentication authentication) {
    return service.gets(authentication.getName());
  }
}
