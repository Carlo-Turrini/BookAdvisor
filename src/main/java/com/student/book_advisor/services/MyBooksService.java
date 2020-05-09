package com.student.book_advisor.services;

import com.student.book_advisor.dto.MyBooksDTO;
import com.student.book_advisor.enums.BookShelf;

import java.util.List;

public interface MyBooksService {
    public String addToShelf(Long userID, Long bookID, BookShelf shef);

    public void deleteFromShelf(Long userID, Long myBookID);

    public String updateShelf(Long userID, Long myBookID, BookShelf shelf);

    public List<MyBooksDTO> findAllMyBooks(Long userID);
}
