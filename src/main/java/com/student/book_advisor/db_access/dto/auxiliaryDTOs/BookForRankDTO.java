package com.student.book_advisor.db_access.dto.auxiliaryDTOs;

public class BookForRankDTO {
    private Integer myBookID;
    private Integer rank;

    public Integer getMyBookID() {
        return myBookID;
    }

    public void setMyBookID(Integer myBookID) {
        this.myBookID = myBookID;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
