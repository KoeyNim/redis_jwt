package com.example.demo.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Bean
  RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory conn) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(conn);

    // 한글 깨짐 방지
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    return template;
  }
}
