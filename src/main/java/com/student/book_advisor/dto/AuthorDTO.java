package com.student.book_advisor.dto;

public class AuthorDTO {
    private Integer id;
    private String authorsFullname;
    private String biography;
    private Integer birthYear;
    private Integer deathYear;
    private String authorsPhoto;

    public AuthorDTO(Integer id, String authorsFullname, String biography, Integer birthYear, Integer deathYear){
        this.id = id;
        this.authorsFullname = authorsFullname;
        this.biography = biography;
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

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
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
