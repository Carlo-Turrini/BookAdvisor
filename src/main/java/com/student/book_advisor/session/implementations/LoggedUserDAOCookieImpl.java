package com.student.book_advisor.session.implementations;

import com.student.book_advisor.session.LoggedUserDAO;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class LoggedUserDAOCookieImpl implements LoggedUserDAO {
    HttpServletRequest request;
    HttpServletResponse response;

    public LoggedUserDAOCookieImpl(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public void create(String authToken) {

        Cookie cookie;
        cookie = new Cookie("authToken", authToken);
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    @Override
    public void destroy() {

        Cookie cookie;
        cookie = new Cookie("authToken", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    @Override
    public String find() {

        Cookie[] cookies = request.getCookies();
        String authToken = null;

        if (cookies != null) {
            for (int i = 0; i < cookies.length && authToken == null; i++) {
                if (cookies[i].getName().equals("authToken")) {
                    authToken = cookies[i].getValue();
                }
            }
        }
        return authToken;

    }


}
