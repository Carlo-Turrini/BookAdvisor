package com.student.book_advisor.dto;

import com.student.book_advisor.dto.auxiliaryDTOs.AuthorOfBook;

import java.util.List;

public class BookRankingDTO {
    private Long id;
    private Long bookID;
    private Integer bookRank;
    private String bookTitle;
    private String bookCoverPhoto;
    private List<AuthorOfBook> bookAuthors;

    public BookRankingDTO(Long id, Integer bookRank, Long bookID, String bookTitle) {
        this.id = id;
        this.bookRank = bookRank;
        this.bookID = bookID;
        this.bookTitle = bookTitle;
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

    public String getBookCoverPhoto() {
        return bookCoverPhoto;
    }

    public void setBookCoverPhoto(String bookCoverPhoto) {
        this.bookCoverPhoto = bookCoverPhoto;
    }

    public List<AuthorOfBook> getBookAuthors() {
        return bookAuthors;
    }

    public void setBookAuthors(List<AuthorOfBook> bookAuthors) {
        this.bookAuthors = bookAuthors;
    }

}
