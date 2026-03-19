package com.example.demo.biz.member;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberEnum {

  ADMIN1("admin", "$2a$10$u5rWSXWNTZtRsivtPTUBlO954TvdrvrrYZeVPqv4OY/4ZdMU8bJ6G", "ADMIN"), // pw 1245678

  USER1("myuser", "$2a$10$tywLwuQxfBUqGjTKFqesLeaOEzbKeJTPh92yImATStTj50sAHNHN2", "USER"), // pw 123123
  USER2("user2", "$2a$10$u5rWSXWNTZtRsivtPTUBlO954TvdrvrrYZeVPqv4OY/4ZdMU8bJ6G", "USER"), // pw 1245678
  ;

  private final String username;
  private final String password;
  private final String role;

  public static UserDetails getUserDetails(MemberEnum memberEnum) {
    return User.builder()
        .username(memberEnum.getUsername())
        .password(memberEnum.getPassword())
        .roles(memberEnum.getRole())
        .build();
  }
}
