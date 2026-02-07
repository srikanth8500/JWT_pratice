package com.example.JWT.Authentication;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.JWT.Token.JWTToken;
import com.example.JWT.Utils.JWTUtils;


public class JWTAuthenticationProvider implements AuthenticationProvider {

    private final JWTUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JWTAuthenticationProvider(JWTUtils jwtUtils, UserDetailsService userDetailsService)
    {
        this.jwtUtils=jwtUtils;
        this.userDetailsService = userDetailsService;
    }


    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = ((JWTToken) authentication).getToken();
        String name= jwtUtils.vaildateToken(token);
        if(name == null)
        {
            throw new BadCredentialsException("JWT token is worng");
        }
        UserDetails user = userDetailsService.loadUserByUsername(name);
        return new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities() );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JWTToken.class.isAssignableFrom(authentication);
    }

}
