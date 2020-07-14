package com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs;

public class MyBooksReadDTO {
    private Integer myBooksID;
    private String title;

    public MyBooksReadDTO(Integer myBooksID, String title) {
        this.myBooksID = myBooksID;
        this.title = title;
    }

    public Integer getMyBooksID() {
        return myBooksID;
    }

    public void setMyBooksID(Integer myBooksID) {
        this.myBooksID = myBooksID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
