package com.student.book_advisor.security;

public class SecurityConstants {
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 24*3600;
    public static final String SIGNING_KEY = "bookadvisorsqlserver";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String AUTHORITIES_KEY = "scopes";
}
