package com.student.book_advisor.dto;

public class AuthorDTO {
    private Long id;
    private String authorsFullname;
    private String bibliography;
    private Integer birthYear;
    private Integer deathYear;
    private String authorsPhoto;

    public AuthorDTO(Long id, String authorsFullname, String bibliography, Integer birthYear, Integer deathYear){
        this.id = id;
        this.authorsFullname = authorsFullname;
        this.bibliography = bibliography;
        this.birthYear = birthYear;
        this.deathYear = deathYear;
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

    public String getBibliography() {
        return bibliography;
    }

    public void setBibliography(String bibliography) {
        this.bibliography = bibliography;
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
