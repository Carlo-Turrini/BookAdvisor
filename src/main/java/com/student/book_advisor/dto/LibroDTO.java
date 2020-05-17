package com.student.book_advisor.dto;

import com.student.book_advisor.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.enums.BookShelf;
import com.student.book_advisor.enums.GenereLibro;

import java.util.List;

public class LibroDTO {

    private Integer id;
    private String titolo;
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
    private List<AuthorOfBook> authors;
    private List<String> genres;
    private BookShelf shelf;

    public LibroDTO(Integer id, String titolo, Integer annoPubblicazione, Integer pagine, String sinossi, String titoloSaga, Integer numInSaga, Double overallRating, Double overallWritingQualityRating, Double overallPageTurnerRating, Double overallOriginalityRating) {
        this.id = id;
        this.titolo = titolo;
        this.annoPubblicazione = annoPubblicazione;
        this.pagine = pagine;
        this.sinossi = sinossi;
        this.titoloSaga = titoloSaga;
        this.numInSaga = numInSaga;
        this.overallRating = overallRating;
        this.overallWritingQualityRating = overallWritingQualityRating;
        this.overallPageTurnerRating = overallPageTurnerRating;
        this.overallOriginalityRating = overallOriginalityRating;
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

    public BookShelf getShelf() {
        return shelf;
    }

    public void setShelf(BookShelf shelf) {
        this.shelf = shelf;
    }

    public Boolean getSaga() {
        return saga;
    }

    public void setSaga(Boolean saga) {
        this.saga = saga;
    }
}
