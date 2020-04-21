package com.student.book_advisor.dto;

public class AuthorDTO {
    private Long id;
    private String authorsName;
    private String authorsSurname;
    private String bibliography;
    private Integer birthYear;
    private Integer deathYear;
    private String authorsPhoto;

    public AuthorDTO(Long id, String authorsName, String authorsSurname, String bibliography, Integer birthYear, Integer deathYear){
        this.id = id;
        this.authorsName = authorsName;
        this.authorsSurname = authorsSurname;
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
