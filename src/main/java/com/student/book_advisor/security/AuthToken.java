package com.student.book_advisor.security;

public class AuthToken {
    private String jwtToken;

    public AuthToken() {

    }
    public AuthToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
