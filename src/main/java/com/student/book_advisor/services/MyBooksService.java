package com.student.book_advisor.services;

import com.student.book_advisor.dto.MyBooksDTO;
import com.student.book_advisor.dto.auxiliaryDTOs.MyBooksReadDTO;
import com.student.book_advisor.enums.BookShelf;

import java.util.List;

public interface MyBooksService {
    public String addToShelf(Integer userID, Integer bookID, BookShelf shef);

    public void deleteFromShelf(Integer userID, Integer myBookID);

    public String updateShelf(Integer userID, Integer myBookID, BookShelf shelf);

    public List<MyBooksDTO> findAllMyBooks(Integer userID);

    public List<MyBooksReadDTO> findAllMyBooksRead(Integer userID);
}
