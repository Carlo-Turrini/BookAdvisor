package com.student.book_advisor.dto;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class RecensioneDTO {
    private Long id;
    private String testo;
    private Integer rating;
    private Integer originalityRating;
    private Integer writingQualityRating;
    private Integer pageTurnerRating;
    private Boolean containsSpoilers;
    private Timestamp timestamp;
    private List<CommentDTO> comments;
    //Info utente
    private Long userId;
    private String username;
    private String profileImage;

    //Info Libro
    private Long bookId;
    private String titolo;
    private List<String> authors;
    private String coverImage;



    public RecensioneDTO(Long id, String testo, Integer rating, Date timestamp, Long userId, String username, Long bookId, String titolo, Integer originalityRating, Integer writingQualityRating, Integer pageTurnerRating, Boolean containsSpoilers) {
        this.id = id;
        this.testo = testo;
        this.rating = rating;
        this.timestamp = new Timestamp(timestamp.getTime());
        this.userId = userId;
        this.username = username;
        this.bookId = bookId;
        this.titolo = titolo;
        this.originalityRating = originalityRating;
        this.writingQualityRating = writingQualityRating;
        this.pageTurnerRating = pageTurnerRating;
        this.containsSpoilers = containsSpoilers;
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

    public Integer getOriginalityRating() {
        return originalityRating;
    }

    public void setOriginalityRating(Integer originalityRating) {
        this.originalityRating = originalityRating;
    }

    public Integer getWritingQualityRating() {
        return writingQualityRating;
    }

    public void setWritingQualityRating(Integer writingQualityRating) {
        this.writingQualityRating = writingQualityRating;
    }

    public Integer getPageTurnerRating() {
        return pageTurnerRating;
    }

    public void setPageTurnerRating(Integer pageTurnerRating) {
        this.pageTurnerRating = pageTurnerRating;
    }

    public Boolean getContainsSpoilers() {
        return containsSpoilers;
    }

    public void setContainsSpoilers(Boolean containsSpoilers) {
        this.containsSpoilers = containsSpoilers;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
