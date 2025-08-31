package com.example.rubiesmanagement.config;

import com.example.rubiesmanagement.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors() // Bật CORS để tích hợp với WebConfig
                .and()
                .authorizeHttpRequests(auth -> auth
                        // Permit tất cả request OPTIONS mà không cần xác thực
                        .requestMatchers("/uploads/**").permitAll() // cho phép tất cả truy cập ảnh
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Permit các endpoint không cần login
                        .requestMatchers(
                                "/api/v1/users/login",
                                "/api/v1/users/register"
                        ).permitAll()

                        // Permit GET các danh mục, sản phẩm, màu sắc, user info
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/products/**",
                                "/api/v1/categories/**",
                                "/api/v1/colors/**",
                                "/api/v1/users"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/users/forgot-password"
                        ).permitAll()

                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/users/reset-password",
                                "/api/v1/users/profile",
                                "api/v1/users/change-password"
                        ).permitAll()

                        // ADMIN thêm, sửa, xóa
                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/products/**",
                                "/api/v1/colors/**",
                                "/api/v1/categories/**",
                                "/api/v1/users/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/categories/**",
                                "/api/v1/products/**",
                                "/api/v1/colors/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/products/**",
                                "/api/v1/colors/**",
                                "/api/v1/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/users/me"
                        ).authenticated()

                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/users/**"
                        ).hasRole("ADMIN")
                        // Còn lại cần login
                        .anyRequest().authenticated()
                )
                // Thêm filter JWT trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}