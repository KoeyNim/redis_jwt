package com.example.demo.biz.board.dto;

import lombok.Data;

@Data
public class BoardResponse {
  private final String title;
  private final String contents;
}
