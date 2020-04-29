package com.student.book_advisor.security;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    private static final long serialVersionUID = -761503632186596342L;

    public JwtAuthenticationException(String e) {
        super(e);
    }
}
