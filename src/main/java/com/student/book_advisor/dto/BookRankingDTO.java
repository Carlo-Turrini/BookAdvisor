package com.student.book_advisor.dto;

import java.util.List;

public class BookRankingDTO {
    private Long id;
    private Long bookID;
    private Integer bookRank;
    private String bookTitle;
    private String bookCoverPhotoPath;
    private List<String> bookAuthors;
    private Double bookOverallRating;

    public BookRankingDTO(Long id, Integer bookRank, Long bookID, String bookTitle, String bookCoverPhotoPath) {
        this.id = id;
        this.bookRank = bookRank;
        this.bookID = bookID;
        this.bookTitle = bookTitle;
        this.bookCoverPhotoPath = bookCoverPhotoPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookID() {
        return bookID;
    }

    public void setBookID(Long bookID) {
        this.bookID = bookID;
    }

    public Integer getBookRank() {
        return bookRank;
    }

    public void setBookRank(Integer bookRank) {
        this.bookRank = bookRank;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookCoverPhotoPath() {
        return bookCoverPhotoPath;
    }

    public void setBookCoverPhotoPath(String bookCoverPhotoPath) {
        this.bookCoverPhotoPath = bookCoverPhotoPath;
    }

    public List<String> getBookAuthors() {
        return bookAuthors;
    }

    public void setBookAuthors(List<String> bookAuthors) {
        this.bookAuthors = bookAuthors;
    }

    public Double getBookOverallRating() {
        return bookOverallRating;
    }

    public void setBookOverallRating(Double bookOverallRating) {
        this.bookOverallRating = bookOverallRating;
    }
}
