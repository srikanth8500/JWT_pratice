package com.example.JWT.JWTFilters;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.JWT.Token.JWTToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTValidationFilter extends OncePerRequestFilter{
    private final AuthenticationManager authenticationManager;
    public JWTValidationFilter(AuthenticationManager authenticationManager)
    {
        this.authenticationManager=authenticationManager;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                String header = request.getHeader("Authentication");
                String token = extractToken(header);
                if(token != null)
                {
                    JWTToken jwtToken = new JWTToken(token);
                    Authentication authResult = authenticationManager.authenticate(jwtToken);
                    if(authResult.isAuthenticated())
                    {
                        SecurityContextHolder.getContext().setAuthentication(authResult);
                    }
                    
                }
                filterChain.doFilter(request, response);
    }

    public String extractToken (String token)
    {
        if(token !=null && token.startsWith("Bearer"))
        {
            return token.substring(7);
        }
        return null;
    }

}
