package com.example.JWT.Securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.JWT.Authentication.JWTAuthenticationProvider;
import com.example.JWT.JWTFilters.JWTAuthFilter;
import com.example.JWT.JWTFilters.JWTRefreshToken;
import com.example.JWT.JWTFilters.JWTValidationFilter;
import com.example.JWT.Utils.JWTUtils;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTUtils jwtUtils;
    private final UserDetailsService userDetailsService;


    public SecurityConfig(JWTUtils jwtUtils , UserDetailsService userDetailsService)
    {

        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JWTAuthenticationProvider jwtAuthenticationProvider(){
        return new JWTAuthenticationProvider(jwtUtils, userDetailsService);
    }
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider()
    {
        return new DaoAuthenticationProvider(userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager()
    {
        return new ProviderManager(Arrays.asList(
            daoAuthenticationProvider(),
            jwtAuthenticationProvider()));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager, JWTUtils jwtUtils) throws Exception
    {
        JWTAuthFilter jwtAuthFilter = new JWTAuthFilter(jwtUtils, authenticationManager);
        JWTValidationFilter jwtValidationFilter = new JWTValidationFilter(authenticationManager);
        JWTRefreshToken jwtRefreshToken = new JWTRefreshToken(jwtUtils, authenticationManager);
        http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth -> auth
            .requestMatchers("/api/register").permitAll()
            .anyRequest().authenticated()
        )
        .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(jwtValidationFilter, JWTAuthFilter.class)
        .addFilterAfter(jwtRefreshToken, JWTValidationFilter.class);
        return http.build();
    }
}
