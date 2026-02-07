package com.example.JWT.Token;

import java.util.Collections;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JWTToken extends AbstractAuthenticationToken {

    String token;
    public JWTToken(String token) {
        super(Collections.emptyList());
        this.token = token;
        setAuthenticated(false);
    }
    public String getToken() {
        return token;
    }
    @Override
    public @Nullable Object getCredentials() {
        return token;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return null;
    }

}
