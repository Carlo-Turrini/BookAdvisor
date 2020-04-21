package com.student.book_advisor.dto;

import java.sql.Timestamp;
import java.util.Date;

public class CommentDTO {
    private Long id;
    private Long userID;
    private Long reviewID;
    private String text;
    private Timestamp createdDate;
    private String usersPhoto;

    public CommentDTO(Long id, Long userID, Long reviewID, String text, Date createdDate) {
        this.id = id;
        this.userID = userID;
        this.reviewID = reviewID;
        this.text = text;
        this.createdDate = new Timestamp(createdDate.getTime());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getReviewID() {
        return reviewID;
    }

    public void setReviewID(Long reviewID) {
        this.reviewID = reviewID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getUsersPhoto() {
        return usersPhoto;
    }

    public void setUsersPhoto(String usersPhoto) {
        this.usersPhoto = usersPhoto;
    }
}
