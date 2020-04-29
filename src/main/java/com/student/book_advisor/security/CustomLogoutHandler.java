package com.student.book_advisor.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomLogoutHandler implements LogoutHandler {
    private JwtTokenProvider jwtTokenProvider;

    public CustomLogoutHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(null);
        jwtTokenProvider.invalidateJwtToken(httpServletRequest, httpServletResponse);
    }
}
