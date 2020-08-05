package com.student.book_advisor.security;

import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class DefaultRequiresCsrfMatcher implements RequestMatcher {
    private static final Pattern allowedMethods = Pattern.compile("^(HEAD|TRACE|OPTIONS|GET)$");

    @Override
    public boolean matches(HttpServletRequest httpServletRequest) {
        return !allowedMethods.matcher(httpServletRequest.getMethod()).matches();
    }
}
