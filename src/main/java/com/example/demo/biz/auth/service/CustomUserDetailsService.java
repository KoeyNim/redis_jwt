package com.example.demo.biz.auth.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  // 임시 유저 객체 생성 DB 사용 X
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      if ("myuser".equals(username)) {
          return User.builder()
                  .username("myuser")
                  .password(new BCryptPasswordEncoder().encode("123123"))
                  .roles("USER")
                  .build();
      }
      throw new UsernameNotFoundException("User not found");
  }
}
