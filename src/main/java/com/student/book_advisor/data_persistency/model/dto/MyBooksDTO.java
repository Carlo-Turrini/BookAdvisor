package com.student.book_advisor.data_persistency.model.dto;


import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.enums.BookShelf;

import java.util.List;


public class MyBooksDTO {
    private Integer id;
    private String titolo;
    private List<AuthorOfBook> authors;
    private List<String> genres;
    private Double overallRating = 0.0;
    private String coverImage;
    private BookShelf shelf;
    private Integer bookID;
    private Integer userID;


    public MyBooksDTO(Integer id, Integer bookID, String titolo, BookShelf shelf, Integer userID, Double overallRating) {
        this.id = id;
        this.titolo = titolo;
        this.shelf = shelf;
        this.bookID = bookID;
        this.userID = userID;
        if(overallRating != null) {
            this.overallRating = overallRating;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public List<AuthorOfBook> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorOfBook> authors) {
        this.authors = authors;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public Double getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Double overallRating) {
        this.overallRating = overallRating;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public BookShelf getShelf() {
        return shelf;
    }

    public void setShelf(BookShelf shelf) {
        this.shelf = shelf;
    }

    public Integer getBookID() {
        return bookID;
    }

    public void setBookID(Integer bookID) {
        this.bookID = bookID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }
}
