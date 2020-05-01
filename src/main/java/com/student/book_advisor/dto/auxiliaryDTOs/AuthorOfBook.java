package com.student.book_advisor.dto.auxiliaryDTOs;

public class AuthorOfBook {
    private Long id;
    private String authorsFullname;

    public AuthorOfBook(Long id, String authorsFullname) {
        this.id = id;
        this.authorsFullname = authorsFullname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorsFullname() {
        return authorsFullname;
    }

    public void setAuthorsFullname(String authorsFullname) {
        this.authorsFullname = authorsFullname;
    }
}
