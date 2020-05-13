package com.student.book_advisor.dto.auxiliaryDTOs;

public class MyBooksReadDTO {
    private Long myBooksID;
    private String title;

    public MyBooksReadDTO(Long myBooksID, String title) {
        this.myBooksID = myBooksID;
        this.title = title;
    }

    public Long getMyBooksID() {
        return myBooksID;
    }

    public void setMyBooksID(Long myBooksID) {
        this.myBooksID = myBooksID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
