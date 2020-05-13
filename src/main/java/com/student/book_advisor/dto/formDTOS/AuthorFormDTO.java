package com.student.book_advisor.dto.formDTOS;

import javax.validation.constraints.*;

public class AuthorFormDTO {
    @NotBlank
    @Size(min = 3, max = 50)
    private String authorsFullname;

    @NotNull
    @Min(-2000)
    @Max(9998)
    private Integer birthYear;

    @Min(-1999)
    @Max(9999)
    private Integer deathYear;

    @NotBlank
    @Size(min = 1, max = 4000)
    private String biography;

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

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
