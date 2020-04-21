package com.student.book_advisor.dto;

import java.util.List;

public class ReadingBookDTO {
    private Long id;
    private Long userID;
    private Long bookID;
    private Integer pagesRead;
    private Integer pagesInBook;
    private String title;
    private String coverPhoto;
    private List<String> authors;
    private Double overallRating = 0.0;

    public ReadingBookDTO (Long id, Long userID, Long bookID, Integer pagesRead, Integer pagesInBook, String title) {
        this.id = id;
        this.userID = userID;
        this.bookID = bookID;
        this.pagesRead = pagesRead;
        this.pagesInBook = pagesInBook;
        this.title = title;
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

    public Long getBookID() {
        return bookID;
    }

    public void setBookID(Long bookID) {
        this.bookID = bookID;
    }

    public Integer getPagesRead() {
        return pagesRead;
    }

    public void setPagesRead(Integer pagesRead) {
        this.pagesRead = pagesRead;
    }

    public Integer getPagesInBook() {
        return pagesInBook;
    }

    public void setPagesInBook(Integer pagesInBook) {
        this.pagesInBook = pagesInBook;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public Double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }
}
