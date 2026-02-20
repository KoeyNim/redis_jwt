package com.example.demo.biz.board.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/board")
public class BoardController {

  @GetMapping
  public String get() {
    return "get ok";
  }

  @GetMapping("list")
  public String gets() {
    return "gets ok";
  }
}
