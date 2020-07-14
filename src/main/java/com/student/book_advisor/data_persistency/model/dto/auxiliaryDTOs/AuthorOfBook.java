package com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs;

public class AuthorOfBook {
    private Integer id;
    private String authorsFullname;

    public AuthorOfBook(Integer id, String authorsFullname) {
        this.id = id;
        this.authorsFullname = authorsFullname;
    }
    public AuthorOfBook() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthorsFullname() {
        return authorsFullname;
    }

    public void setAuthorsFullname(String authorsFullname) {
        this.authorsFullname = authorsFullname;
    }
}
