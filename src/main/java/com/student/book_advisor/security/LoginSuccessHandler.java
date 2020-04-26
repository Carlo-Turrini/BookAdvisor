package com.student.book_advisor.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
       AuthUserPrincipal userPrincipal = (AuthUserPrincipal) authentication.getPrincipal();
       String authToken = "id:" + userPrincipal.getId() + "roles:";
       Set<SimpleGrantedAuthority> authorities = (Set<SimpleGrantedAuthority>) userPrincipal.getAuthorities();
       Integer authoritiesDim = authorities.size();
       Integer counter = 1;
       for(SimpleGrantedAuthority authority : authorities) {
           authToken = authToken + authority.getAuthority();
           if(counter < authoritiesDim) {
               authToken = authToken + ",";
           }
           counter++;
       }

        Cookie cookie;
        cookie = new Cookie("authToken", authToken);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }
}
