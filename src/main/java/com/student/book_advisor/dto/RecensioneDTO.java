package com.student.book_advisor.dto;

import java.sql.Timestamp;
import java.util.Date;

public class RecensioneDTO {
    private Long id;
    private String testo;
    private Integer rating;
    private Timestamp timestamp;
    //Info utente
    private Long userId;
    private String username;
    private String profileImage;

    //Info Libro
    private Long bookId;
    private String titolo;
    private String autori;
    private String coverImage;



    public RecensioneDTO(Long id, String testo, Integer rating, Date timestamp, Long userId, String username, Long bookId, String titolo, String autori) {
        this.id = id;
        this.testo = testo;
        this.rating = rating;
        this.timestamp = new Timestamp(timestamp.getTime());
        this.userId = userId;
        this.username = username;
        this.bookId = bookId;
        this.titolo = titolo;
        this.autori = autori;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}
