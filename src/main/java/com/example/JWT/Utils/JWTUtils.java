package com.example.JWT.Utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
@SuppressWarnings("deprecation")
public class JWTUtils {

    private final String secret = "this-is-a-very-long-secret-key-with-at-least-32-bytes";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String username, long expiry)
    {
        return Jwts.builder()
        .subject(username)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis()+expiry * 60 * 1000))
        .signWith(key)
        .compact();
    }

    public String vaildateToken(String token)
    {
        try{
        return Jwts.parser()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
        }
        catch(JwtException e)
        {
            return null;
        }

    }



    


}
