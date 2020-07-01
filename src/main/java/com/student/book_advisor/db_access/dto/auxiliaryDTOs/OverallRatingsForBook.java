package com.student.book_advisor.db_access.dto.auxiliaryDTOs;

public class OverallRatingsForBook {
    private Double overallRating;
    private Double overallWritingQualityRating;
    private Double overallPageTurnerRating;
    private Double overallOriginalityRating;

    public OverallRatingsForBook(Double overallRating, Double overallOriginalityRating, Double overallPageTurnerRating, Double overallWritingQualityRating) {
        this.overallRating = overallRating;
        this.overallOriginalityRating = overallOriginalityRating;
        this.overallPageTurnerRating = overallPageTurnerRating;
        this.overallWritingQualityRating = overallWritingQualityRating;
    }

    public Double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }

    public Double getOverallWritingQualityRating() {
        return overallWritingQualityRating;
    }

    public void setOverallWritingQualityRating(Double overallWritingQualityRating) {
        this.overallWritingQualityRating = overallWritingQualityRating;
    }

    public Double getOverallPageTurnerRating() {
        return overallPageTurnerRating;
    }

    public void setOverallPageTurnerRating(Double overallPageTurnerRating) {
        this.overallPageTurnerRating = overallPageTurnerRating;
    }

    public Double getOverallOriginalityRating() {
        return overallOriginalityRating;
    }

    public void setOverallOriginalityRating(Double overallOriginalityRating) {
        this.overallOriginalityRating = overallOriginalityRating;
    }
}
