package com.example.JWT.JWTFilters;

import java.io.IOException;

import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.JWT.DTO.UserDTO;
import com.example.JWT.Utils.JWTUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter{
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public JWTAuthFilter(JWTUtils jwtUtils, AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils; 
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                String path = request.getServletPath();
                if (path.equals("/api/register") || path.equals("/auth/login")) {
    filterChain.doFilter(request, response);
    return;
}           
if (!path.equals("/generate")) {
    filterChain.doFilter(request, response);
    return;
}    
                ObjectMapper objectMapper = new ObjectMapper();
                UserDTO userDTO = objectMapper.readValue(request.getInputStream(), UserDTO.class);
                log.info("Attempting login for user: " + userDTO.getName());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDTO.getName(), userDTO.getPassword());
                Authentication authResult = authenticationManager.authenticate(authToken);
                if(authResult.isAuthenticated())
                {
                    String token = jwtUtils.generateToken(authResult.getName(), 15);
                    response.setHeader("Authorization", "Bearer "+token);
                    String refreshToken = jwtUtils.generateToken(authResult.getName(), 7*24*60);
                    Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
                    refreshCookie.setHttpOnly(true);
                    refreshCookie.setSecure(false);
                    refreshCookie.setPath("/api/refreshtoken");
                    refreshCookie.setMaxAge(7*24*60*60);
                    response.addCookie(refreshCookie);
                }
                filterChain.doFilter(request, response);

    }

}
