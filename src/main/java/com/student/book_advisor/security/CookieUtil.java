package com.student.book_advisor.security;

import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static void create(HttpServletResponse response, String name, String value, Boolean secure, Boolean httpOnly, Integer maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(secure);
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    public static void clear(HttpServletRequest request, String name) {
        Cookie found = WebUtils.getCookie(request, name);
        found.setMaxAge(0);
    }
    public static String getValue(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }

}
