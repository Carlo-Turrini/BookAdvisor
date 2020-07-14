package com.student.book_advisor.data_persistency.model.dto;

import com.student.book_advisor.security.Credenziali;

public class UtenteDTO {

    private Integer id;
    private String nome;
    private String cognome;
    private String username;
    private String password;
    private String email;
    private String fotoProfilo;
    private Credenziali credenziali;
    private String descrizione;

    public UtenteDTO(Integer id, String nome, String cognome, String username, String password, String email, Credenziali credenziali, String descrizione) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.email = email;
        this.credenziali = credenziali;
        this.descrizione = descrizione;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoProfilo() {
        return fotoProfilo;
    }

    public void setFotoProfilo(String fotoProfilo) {
        this.fotoProfilo = fotoProfilo;
    }

    public Credenziali getCredenziali() {
        return credenziali;
    }

    public void setCredenziali(Credenziali credenziali) {
        this.credenziali = credenziali;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
