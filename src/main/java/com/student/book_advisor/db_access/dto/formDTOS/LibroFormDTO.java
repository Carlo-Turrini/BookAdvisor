package com.student.book_advisor.db_access.dto.formDTOS;


import com.student.book_advisor.db_access.dto.auxiliaryDTOs.AuthorOfBook;

import javax.validation.constraints.*;
import java.util.List;

public class LibroFormDTO {

    @NotNull
    @Min(0)
    private Integer annoPubblicazione;

    @NotNull
    @Min(1)
    @Max(9999)
    private Integer pagine;

    @NotBlank
    @Size(min = 1, max = 128)
    private String titolo;

    @NotBlank
    @Size(min = 1, max = 2048)
    private String sinossi;

    @NotEmpty
    private List<AuthorOfBook> autori;

    @NotNull
    private Boolean saga;

    @Size(max = 64)
    private String titoloSaga;

    @Max(99)
    private Integer numInSaga;

    @NotEmpty
    private List<String> generi;


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

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
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

}
