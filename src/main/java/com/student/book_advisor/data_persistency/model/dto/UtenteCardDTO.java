package com.student.book_advisor.data_persistency.model.dto;


public class UtenteCardDTO {
    private Integer id;
    private String username;
    private String nome;
    private String cognome;
    private String profileImage;


    public UtenteCardDTO(Integer id, String username, String nome, String cognome) {
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
