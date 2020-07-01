package com.student.book_advisor.db_access.dto;

public class AuthorCardDTO {
    private Integer id;
    private String authorsFullname;
    private Integer birthYear;
    private Integer deathYear;
    private String authorsPhoto;

    public AuthorCardDTO(Integer id, String authorsFullname, Integer birthYear, Integer deathYear) {
        this.id = id;
        this.authorsFullname = authorsFullname;
        this.birthYear = birthYear;
        this.deathYear = deathYear;

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

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    public String getAuthorsPhoto() {
        return authorsPhoto;
    }

    public void setAuthorsPhoto(String authorsPhoto) {
        this.authorsPhoto = authorsPhoto;
    }
}
