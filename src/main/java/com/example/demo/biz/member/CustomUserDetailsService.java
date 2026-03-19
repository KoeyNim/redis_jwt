package com.example.demo.biz.member;

import java.util.Objects;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  // 임시 유저 객체 생성 (DB 사용 X)
  @Override
  public UserDetails loadUserByUsername(String username) {
    for (MemberEnum member : MemberEnum.values()) {
      if (Objects.equals(username, member.getUsername())) {
        return MemberEnum.getUserDetails(member);
      }
    }
    throw new UsernameNotFoundException("User not found");
  }
}
