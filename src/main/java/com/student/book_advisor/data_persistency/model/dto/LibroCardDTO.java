package com.student.book_advisor.data_persistency.model.dto;


import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;

import java.util.List;


public class LibroCardDTO {
    private Integer id;
    private String titolo;
    private List<AuthorOfBook> autori;
    private List<String> generi;
    private Double overallRating = 0.0;
    private String coverImage;


    public LibroCardDTO(Integer id, String titolo, Double overallRating) {
        this.id = id;
        this.titolo = titolo;
        if(overallRating != null) {
            this.overallRating = overallRating;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public List<AuthorOfBook> getAutori() {
        return autori;
    }

    public void setAutori(List<AuthorOfBook> autori) {
        this.autori = autori;
    }

    public List<String> getGeneri() {
        return generi;
    }

    public void setGeneri(List<String> generi) {
        this.generi = generi;
    }

    public Double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}
