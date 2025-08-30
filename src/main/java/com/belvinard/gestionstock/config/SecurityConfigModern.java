package com.belvinard.gestionstock.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfigModern {
    
    private final JwtAuthenticationEntryPoint jwtEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(this::configureAuthorization)
            .exceptionHandling(this::configureExceptionHandling)
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .build();
    }
    
    private void configureAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth
            .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
            .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
            .requestMatchers(MANAGER_ENDPOINTS).hasAnyRole("ADMIN", "STOCK_MANAGER", "SALES_MANAGER")
            .anyRequest().authenticated();
    }
    
    private void configureExceptionHandling(org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer<HttpSecurity> ex) {
        ex.authenticationEntryPoint(jwtEntryPoint)
          .accessDeniedHandler(accessDeniedHandler);
    }
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
    
    // Endpoints constants
    private static final String[] PUBLIC_ENDPOINTS = {
        "/api/v1/auth/public/**",
        "/swagger-ui/**",
        "/v3/api-docs/**"
    };
    
    private static final String[] ADMIN_ENDPOINTS = {
        "/api/v1/utilisateurs/**",
        "/api/v1/entreprise/admin/**"
    };
    
    private static final String[] MANAGER_ENDPOINTS = {
        "/api/v1/articles/**",
        "/api/v1/categories/**"
    };
}