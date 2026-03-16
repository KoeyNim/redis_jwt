package com.example.demo.biz.board.repository;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.biz.board.dto.BoardResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardEnum {
  BOARD_1("1", "제목1", "내용1", "myuser"),
  BOARD_2("2", "제목2", "내용2", "myuser"),
  BOARD_3("3", "제목3", "내용3", "user2"),
  BOARD_4("4", "제목4", "내용4", "user2");

  private final String id;
  private final String title;
  private final String contents;
  private final String writer;

  public static BoardResponse getBoard(String id) {
    BoardEnum board = BoardEnum.valueOf("BOARD_" + id);
    return new BoardResponse(board.getTitle(), board.getContents());
  }

  public static List<BoardResponse> getBoards(String writer) {
    List<BoardResponse> boards = new ArrayList<>();
    for (BoardEnum boardEnum : BoardEnum.values()) {
      if (boardEnum.writer.equals(writer)) {
        BoardResponse res = new BoardResponse(boardEnum.getTitle(), boardEnum.getContents());
        boards.add(res);
      }
    }
    return boards;
  }
}
