package com.example.demo.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

public class JwtUsernameAndPasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JwtUsernameAndPasswordAuthFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        try {
            UsernameAndPAsswordAuthReq usernameAndPAsswordAuthReq =
                    new ObjectMapper().readValue(request.getInputStream(), UsernameAndPAsswordAuthReq.class);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    usernameAndPAsswordAuthReq.getUsername(),
                    usernameAndPAsswordAuthReq.getPassword()
            );
            return authenticationManager.authenticate(auth);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String token = Jwts.builder()
                            .setSubject(authResult.getName())
                            .claim("authorities", authResult.getAuthorities())
                            .setIssuedAt(new java.util.Date())
                            .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusWeeks(2)))
                            .signWith(Keys.hmacShaKeyFor("securesecuresecuresecuresecuresecure".getBytes()))
                            .compact();

        response.addHeader("Authorization","Bearer " + token);
    }
}
