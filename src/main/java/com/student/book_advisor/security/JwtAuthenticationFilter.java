package com.student.book_advisor.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.book_advisor.data_persistency.model.dto.LoginDTO;
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

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginDTO loginInfo = new ObjectMapper().readValue(request.getInputStream(), LoginDTO.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginInfo.getUsername(), loginInfo.getPassword()));
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        AuthUserPrincipal authUserPrincipal = (AuthUserPrincipal) authResult;
        String jwtToken = jwtTokenProvider.createToken(authUserPrincipal.getUsername(), authUserPrincipal.getId(), authUserPrincipal.getAuthoritiesToString());
        CookieUtil.create(response, JwtProperties.JWT_TOKEN_COOKIE, jwtToken, false, true, (int) JwtProperties.JWT_EXPIRATION/1000);
    }
}
