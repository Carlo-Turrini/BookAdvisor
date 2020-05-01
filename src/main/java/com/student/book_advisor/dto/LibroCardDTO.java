package com.student.book_advisor.dto;


import com.student.book_advisor.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.enums.GenereLibro;

import java.util.List;


public class LibroCardDTO {
    private Long id;
    private String titolo;
    private List<AuthorOfBook> authors;
    private List<String> genres;
    private Double overallRating = 0.0;
    private String coverImage;


    public LibroCardDTO(Long id, String titolo) {
        this.id = id;
        this.titolo = titolo;
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

    public List<AuthorOfBook> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorOfBook> authors) {
        this.authors = authors;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
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
