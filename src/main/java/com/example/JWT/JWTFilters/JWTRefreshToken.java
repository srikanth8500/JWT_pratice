package com.example.JWT.JWTFilters;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.JWT.Token.JWTToken;
import com.example.JWT.Utils.JWTUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTRefreshToken  extends OncePerRequestFilter{

    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public JWTRefreshToken(JWTUtils jwtUtils, AuthenticationManager authenticationManager)
    {
        this.authenticationManager=authenticationManager;
        this.jwtUtils= jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                if(!request.getServletPath().equals("/api/refreshtoken"))
                {
                    filterChain.doFilter(request, response);
                    return;
                }
        String cookieToken = extractToken(request.getCookies());
        if(cookieToken !=null)
        {
            JWTToken jwtToken = new JWTToken(cookieToken);
            Authentication authResult = authenticationManager.authenticate(jwtToken);
            if(authResult.isAuthenticated())
            {
                String newToken= jwtUtils.generateToken(authResult.getName(), 15);
                response.setHeader("Authentication", "Bearer "+newToken);
            }
        }
        else
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
    }

    private String extractToken(Cookie[] cookies) {
        if(cookies == null)
            return null;

        for(Cookie cookie : cookies)
        {
            if(cookie.getName().equals("refreshToken"))
                return cookie.getValue();
        }
        return null;
    }

}
