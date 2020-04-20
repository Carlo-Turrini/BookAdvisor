package com.student.book_advisor.session.implementations;

import com.student.book_advisor.session.LoggedUserDAO;
import com.student.book_advisor.session.SessionDAOFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieSessionDAOFactory extends SessionDAOFactory {
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Override
    public void initSession(HttpServletRequest request, HttpServletResponse response) {

        try {
            this.request=request;
            this.response=response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public LoggedUserDAO getLoggedUserDAO() {
        return new LoggedUserDAOCookieImpl(request,response);
    }
}
