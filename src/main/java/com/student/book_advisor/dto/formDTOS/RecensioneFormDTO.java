package com.student.book_advisor.dto.formDTOS;

import com.sun.istack.NotNull;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RecensioneFormDTO {

    @NotNull
    @Min(0)
    @Max(5)
    private Integer rating;

    @NotBlank
    @Size(min = 1, max = 2048)
    private String testo;

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
}
