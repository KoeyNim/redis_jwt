package com.example.demo.biz.board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.biz.board.dto.BoardResponse;
import com.example.demo.biz.board.repository.BoardEnum;

@Service
public class BoardService {

  public List<BoardResponse> gets(String writer) {
    return BoardEnum.getBoards(writer);
  }

}
