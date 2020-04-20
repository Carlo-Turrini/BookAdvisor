package com.student.book_advisor.dto;


import com.student.book_advisor.enums.GenereLibro;


public class LibroCardDTO {
    private Long id;
    private String titolo;
    private String autori;
    private GenereLibro genere;
    private Double overallRating = 0.0;
    private String coverImage;


    public LibroCardDTO(Long id, String titolo, String autori, GenereLibro genere) {
        this.id = id;
        this.titolo = titolo;
        this.autori = autori;
        this.genere = genere;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getAutori() {
        return autori;
    }

    public void setAutori(String autori) {
        this.autori = autori;
    }

    public GenereLibro getGenere() {
        return genere;
    }

    public void setGenere(GenereLibro genere) {
        this.genere = genere;
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
