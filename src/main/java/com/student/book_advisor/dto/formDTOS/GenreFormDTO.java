package com.student.book_advisor.dto.formDTOS;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class GenreFormDTO {
    @NotBlank
    @Size(min = 1, max = 20)
    private String genre;

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
