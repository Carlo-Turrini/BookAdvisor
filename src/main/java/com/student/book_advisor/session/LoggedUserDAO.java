package com.student.book_advisor.session;

import java.util.UUID;

public interface LoggedUserDAO {
    public void create(
            String authToken);

    public void destroy();

    public String find();
}
