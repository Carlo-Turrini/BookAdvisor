package com.student.book_advisor.data_persistency.model.dto.formDTOS;

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

    @NotNull
    @Min(0)
    @Max(5)
    private Integer writingQualityRating;

    @NotNull
    @Min(0)
    @Max(5)
    private Integer originalityRating;

    @NotNull
    @Min(0)
    @Max(5)
    private Integer pageTurnerRating;

    @NotNull
    private Boolean containsSpoilers;

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

    public Integer getWritingQualityRating() {
        return writingQualityRating;
    }

    public void setWritingQualityRating(Integer writingQualityRating) {
        this.writingQualityRating = writingQualityRating;
    }

    public Integer getOriginalityRating() {
        return originalityRating;
    }

    public void setOriginalityRating(Integer originalityRating) {
        this.originalityRating = originalityRating;
    }

    public Integer getPageTurnerRating() {
        return pageTurnerRating;
    }

    public void setPageTurnerRating(Integer pageTurnerRating) {
        this.pageTurnerRating = pageTurnerRating;
    }

    public Boolean getContainsSpoilers() {
        return containsSpoilers;
    }

    public void setContainsSpoilers(Boolean containsSpoilers) {
        this.containsSpoilers = containsSpoilers;
    }
}
