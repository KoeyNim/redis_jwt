package com.example.demo.biz.board.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public String gets() {
    return service.gets();
  }
}
