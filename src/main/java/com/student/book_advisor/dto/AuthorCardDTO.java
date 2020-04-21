package com.student.book_advisor.dto;

public class AuthorCardDTO {
    private Long id;
    private String authorsName;
    private String authorsSurname;
    private Integer birthYear;
    private Integer deathYear;
    private String authorsPhoto;

    public AuthorCardDTO(Long id, String authorsName, String authorsSurname, Integer birthYear, Integer deathYear) {
        this.id = id;
        this.authorsName = authorsName;
        this.authorsSurname = authorsSurname;
        this.birthYear = birthYear;
        this.deathYear = deathYear;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorsName() {
        return authorsName;
    }

    public void setAuthorsName(String authorsName) {
        this.authorsName = authorsName;
    }

    public String getAuthorsSurname() {
        return authorsSurname;
    }

    public void setAuthorsSurname(String authorsSurname) {
        this.authorsSurname = authorsSurname;
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
