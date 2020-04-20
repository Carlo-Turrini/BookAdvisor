package com.student.book_advisor.entities;

import com.student.book_advisor.constants.Constants;
import com.student.book_advisor.enums.Credenziali;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "UTENTE", uniqueConstraints = {@UniqueConstraint(columnNames = {"email", "del_token"}), @UniqueConstraint(columnNames = {"username", "del_token"} )})
@Where(clause = "del_token = 00000000-0000-0000-0000-000000000000")
public class Utente implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "del_token", nullable = false)
    @ColumnDefault("0")
    private String delToken = Constants.nilUUID;

    @Column(name = "auth_token", nullable = false, updatable = false)
    private String authToken;

    @Column(length = 24, nullable = false)
    private String nome;

    @Column(length = 24, nullable = false)
    private String cognome;

    @Column(name = "username", length = 24, nullable = false)
    private String username;

    @Email
    @Column(length = 128, nullable = false)
    private String email;

    @Column (length = 64, nullable = false)
    private String password;


    @Enumerated(EnumType.STRING)
    @ColumnDefault("'Normale'")
    @Column(name = "credenziali", nullable = false)
    private Credenziali credenziali = Credenziali.Normale;


    @ColumnDefault(Constants.DEF_PROFILE_PIC)
    @Column(length = 128, name = "profile_photo", nullable = false)
    private String profilePhotoPath = Constants.DEF_PROFILE_PIC;

    @Column(length = 2048)
    private String descrizione;

    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Recensione> recensioneList = new ArrayList<Recensione>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDelToken() {
        return delToken;
    }

    public void setDelToken(String delToken) {
        this.delToken = delToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Credenziali getCredenziali() {
        return credenziali;
    }

    public void setCredenziali(Credenziali credenziali) {
        this.credenziali = credenziali;
    }

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public List<Recensione> getRecensioneList() {
        return recensioneList;
    }

    public void setRecensioneList(List<Recensione> recensioneList) {
        this.recensioneList = recensioneList;
    }
}

