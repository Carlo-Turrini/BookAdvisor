package com.student.book_advisor.dto;

public class PrizeDTO {
    private Integer id;
    private Integer yearAwarded;
    private String prizeName;

    public PrizeDTO(Integer id, Integer yearAwarded, String prizeName) {
        this.id = id;
        this.yearAwarded = yearAwarded;
        this.prizeName = prizeName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
