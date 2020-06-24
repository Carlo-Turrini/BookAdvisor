package com.student.book_advisor.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatelessCsrfFilter extends OncePerRequestFilter {
    private static final String CSRF_TOKEN = "XSRF-TOKEN";
    private static final String X_CSRF_TOKEN = "X-XSRF-TOKEN";
    private final RequestMatcher requireCsrfProtectionMatcher = new DefaultRequiresCsrfMatcher();
    private final AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(requireCsrfProtectionMatcher.matches(request)) {
            String csrfTokenValue = request.getHeader(X_CSRF_TOKEN);
            final String csrfCookie = CookieUtil.getValue(request, CSRF_TOKEN);
            if(csrfTokenValue == null || !csrfTokenValue.equals(csrfCookie)){
                System.out.println("Missing or non-matching CSRF token");
                System.out.println(csrfCookie);
                System.out.println(csrfTokenValue);
                accessDeniedHandler.handle(request, response, new AccessDeniedException("Missing or non-matching CSRF token"));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
