package com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs;

public class BookRankRemovalInfoDTO {
    private Integer userID;
    private Integer bookRankID;

    public BookRankRemovalInfoDTO(Integer userID, Integer bookRankID) {
        this.bookRankID = bookRankID;
        this.userID = userID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getBookRankID() {
        return bookRankID;
    }

    public void setBookRankID(Integer bookRankID) {
        this.bookRankID = bookRankID;
    }
}
