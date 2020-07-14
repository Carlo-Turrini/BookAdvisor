package com.student.book_advisor.data_persistency.model.dto;

import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecensioneDTO {
    private Integer id;
    private String testo;
    private Integer rating;
    private Integer originalityRating;
    private Integer writingQualityRating;
    private Integer pageTurnerRating;
    private Boolean containsSpoilers;
    private String timestamp;
    private Long numOfUsersConsideredReviewUseful;
    private Boolean isReviewUsefulForLoggedUser;
    //Info utente
    private Integer userId;
    private String username;
    private String profileImage;

    //Info Libro
    private Integer bookId;
    private String titolo;
    private List<AuthorOfBook> autori;
    private String coverImage;



    public RecensioneDTO(Integer id, String testo, Integer rating, Date timestamp, Integer userId, String username, Integer bookId, String titolo, Integer originalityRating, Integer writingQualityRating, Integer pageTurnerRating, Boolean containsSpoilers, Long numOfUsersConsideredReviewUseful) {
        this.id = id;
        this.testo = testo;
        this.rating = rating;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        this.timestamp = dateFormat.format(timestamp);
        this.userId = userId;
        this.username = username;
        this.bookId = bookId;
        this.titolo = titolo;
        this.originalityRating = originalityRating;
        this.writingQualityRating = writingQualityRating;
        this.pageTurnerRating = pageTurnerRating;
        this.containsSpoilers = containsSpoilers;
        this.numOfUsersConsideredReviewUseful = numOfUsersConsideredReviewUseful;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
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

    public List<AuthorOfBook> getAutori() {
        return autori;
    }

    public void setAutori(List<AuthorOfBook> autori) {
        this.autori = autori;
    }

    public Long getNumOfUsersConsideredReviewUseful() {
        return numOfUsersConsideredReviewUseful;
    }

    public void setNumOfUsersConsideredReviewUseful(Long numOfUsersConsideredReviewUseful) {
        this.numOfUsersConsideredReviewUseful = numOfUsersConsideredReviewUseful;
    }

    public Boolean getReviewUsefulForLoggedUser() {
        return isReviewUsefulForLoggedUser;
    }

    public void setReviewUsefulForLoggedUser(Boolean reviewUsefulForLoggedUser) {
        isReviewUsefulForLoggedUser = reviewUsefulForLoggedUser;
    }
}
