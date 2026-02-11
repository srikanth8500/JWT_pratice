package com.example.JWT.contoller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.JWT.DTO.UserDTO;
import com.example.JWT.Token.JWTToken;
import com.example.JWT.Utils.JWTUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, JWTUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody UserDTO dto, HttpServletRequest request, HttpServletResponse response ) {

        System.out.println("Attempting login for user: " + dto.getName());

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getName(), dto.getPassword())
        );

        String accessToken = jwtUtils.generateToken(authentication.getName(), 15);
        String refreshToken = jwtUtils.generateToken(authentication.getName(), 7 * 24 * 60);

        Map<String, String> response1 = new HashMap<>();
        response1.put("accessToken", accessToken);
        response1.put("refreshToken", refreshToken);
        response.setHeader("Authorization", "Bearer "+accessToken);
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
                    refreshCookie.setHttpOnly(true);
                    refreshCookie.setSecure(false);
                    refreshCookie.setPath("/api/refreshtoken");
                    refreshCookie.setMaxAge(7*24*60*60);
                    response.addCookie(refreshCookie);
        return response1;


    }

    @PostMapping("/refresh")
    public String postMethodName(HttpServletRequest request, HttpServletResponse response) {

        String cookieToken = extractToken(request.getCookies());
        if(cookieToken !=null)
        {
            JWTToken jwtToken = new JWTToken(cookieToken);
            Authentication authResult = authenticationManager.authenticate(jwtToken);
            if(authResult.isAuthenticated())
            {
                String newToken= jwtUtils.generateToken(authResult.getName(), 15);
                response.setHeader("Authorization", "Bearer "+newToken);
                return "Token refreshed";
            }
        }
        else
        {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "Unauthorized";
        }
        return "Unauthorized";
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
