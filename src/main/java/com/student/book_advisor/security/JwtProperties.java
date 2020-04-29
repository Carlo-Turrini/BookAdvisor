package com.student.book_advisor.security;

import org.springframework.stereotype.Component;

@Component
public final class JwtProperties {
    public static final String JWT_SECRET = "bookadvisor";
    public static  final long JWT_EXPIRATION = 3600000;
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_STARTS_WITH = "Bearer ";
    public static final String JWT_TOKEN_COOKIE = "access_token";
}
