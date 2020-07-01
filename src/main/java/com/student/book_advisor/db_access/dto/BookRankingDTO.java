package com.student.book_advisor.db_access.dto;

import com.student.book_advisor.db_access.dto.auxiliaryDTOs.AuthorOfBook;

import java.util.List;

public class BookRankingDTO {
    private Integer id;
    private Integer bookID;
    private Integer bookRank;
    private String bookTitle;
    private String bookCoverPhoto;
    private List<AuthorOfBook> bookAuthors;

    public BookRankingDTO(Integer id, Integer bookRank, Integer bookID, String bookTitle) {
        this.id = id;
        this.bookRank = bookRank;
        this.bookID = bookID;
        this.bookTitle = bookTitle;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookID() {
        return bookID;
    }

    public void setBookID(Integer bookID) {
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
