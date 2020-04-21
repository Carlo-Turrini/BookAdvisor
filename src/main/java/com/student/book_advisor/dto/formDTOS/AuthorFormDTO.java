package com.student.book_advisor.dto.formDTOS;

import javax.validation.constraints.*;

public class AuthorFormDTO {
    @NotBlank
    @Size(min = 1, max = 20)
    private String authorsName;

    @NotBlank
    @Size(min = 1, max = 20)
    private String authorsSurname;

    @NotNull
    @Min(-2000)
    @Max(9998)
    private Integer birthYear;

    @Min(-1999)
    @Max(9999)
    private Integer deathYear;

    @NotBlank
    @Size(min = 1, max = 4000)
    private String bibliography;

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

    public String getBibliography() {
        return bibliography;
    }

    public void setBibliography(String bibliography) {
        this.bibliography = bibliography;
    }
}
