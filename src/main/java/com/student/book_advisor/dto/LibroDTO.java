package com.student.book_advisor.dto;

import com.student.book_advisor.enums.GenereLibro;

import java.util.List;

public class LibroDTO {

    private Long id;
    private String titolo;
    private String isbn;
    private Integer annoPubblicazione;
    private Integer pagine;
    private String sinossi;
    private Boolean saga;
    private String titoloSaga;
    private Integer numInSaga;
    private String copertina;
    private Double overallRating;
    private Double overallWritingQualityRating;
    private Double overallPageTurnerRating;
    private Double overallOriginalityRating;
    private List<String> authors;
    private List<String> genres;

    public LibroDTO(Long id, String titolo, String isbn, Integer annoPubblicazione, Integer pagine, String sinossi, Boolean saga, String titoloSaga, Integer numInSaga) {
        this.id = id;
        this.titolo = titolo;
        this.isbn = isbn;
        this.annoPubblicazione = annoPubblicazione;
        this.pagine = pagine;
        this.sinossi = sinossi;
        this.saga = saga;
        this.titoloSaga = titoloSaga;
        this.numInSaga = numInSaga;
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

    public Integer getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public void setAnnoPubblicazione(Integer annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }

    public Integer getPagine() {
        return pagine;
    }

    public void setPagine(Integer pagine) {
        this.pagine = pagine;
    }

    public String getSinossi() {
        return sinossi;
    }

    public void setSinossi(String sinossi) {
        this.sinossi = sinossi;
    }

    public Boolean getSaga() {
        return saga;
    }

    public void setSaga(Boolean saga) {
        this.saga = saga;
    }

    public String getTitoloSaga() {
        return titoloSaga;
    }

    public void setTitoloSaga(String titoloSaga) {
        this.titoloSaga = titoloSaga;
    }

    public Integer getNumInSaga() {
        return numInSaga;
    }

    public void setNumInSaga(Integer numInSaga) {
        this.numInSaga = numInSaga;
    }

    public String getCopertina() {
        return copertina;
    }

    public void setCopertina(String copertina) {
        this.copertina = copertina;
    }

    public Double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
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

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
}
