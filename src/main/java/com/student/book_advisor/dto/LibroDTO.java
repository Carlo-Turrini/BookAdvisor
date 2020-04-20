package com.student.book_advisor.dto;

import com.student.book_advisor.enums.GenereLibro;

public class LibroDTO {

    private Long id;
    private String titolo;
    private String autori;
    private Integer annoPubblicazione;
    private GenereLibro genere;
    private Integer pagine;
    private String sinossi;
    private Boolean saga;
    private String titoloSaga;
    private Integer numInSaga;
    private String copertina;
    private Double overallRating;

    public LibroDTO(Long id, String titolo, String autori, Integer annoPubblicazione, Integer pagine, GenereLibro genere, String sinossi, Boolean saga, String titoloSaga, Integer numInSaga) {
        this.id = id;
        this.titolo = titolo;
        this.autori = autori;
        this.annoPubblicazione = annoPubblicazione;
        this.pagine = pagine;
        this.genere = genere;
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

    public String getAutori() {
        return autori;
    }

    public void setAutori(String autori) {
        this.autori = autori;
    }

    public Integer getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public void setAnnoPubblicazione(Integer annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }

    public GenereLibro getGenere() {
        return genere;
    }

    public void setGenere(GenereLibro genere) {
        this.genere = genere;
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
}
