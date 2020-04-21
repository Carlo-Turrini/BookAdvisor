package com.student.book_advisor.dto.formDTOS;

import javax.validation.constraints.*;

public class PrizeFormDTO {
    @NotNull
    @Min(0)
    @Max(9999)
    private Integer yearAwarded;

    @NotBlank
    @Size(min = 1, max = 64)
    private String prizeName;

    public Integer getYearAwarded() {
        return yearAwarded;
    }

    public void setYearAwarded(Integer yearAwarded) {
        this.yearAwarded = yearAwarded;
    }

    public String getPrizeName() {
        return prizeName;
    }

    public void setPrizeName(String prizeName) {
        this.prizeName = prizeName;
    }
}
