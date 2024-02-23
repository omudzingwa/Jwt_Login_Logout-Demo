package com.quad.login_logout.configs;

import com.quad.login_logout.jwtutils.JwtAccessDeniedHandler;
import com.quad.login_logout.jwtutils.JwtAuthenticationEntryPoint;
import com.quad.login_logout.jwtutils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private String redisPort;

    // lettuce
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, Integer.parseInt(redisPort));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        //Get redisTemplate and use set, get, delete
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //set setKeySerializer, setValueSerializer settings
        // Prevents data from being output in an unrecognizable format when viewing data directly through redis-cli
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Configuration
    @RequiredArgsConstructor
    @EnableWebSecurity
    public static class JwtWebSecurity {
        private final JwtTokenProvider jwtTokenProvider;
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
        private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    }
}
