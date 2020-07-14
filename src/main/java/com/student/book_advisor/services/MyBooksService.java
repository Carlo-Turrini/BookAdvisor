package com.student.book_advisor.services;

import com.student.book_advisor.data_persistency.model.dto.MyBooksDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.MyBooksReadDTO;
import com.student.book_advisor.enums.BookShelf;

import java.util.List;

public interface MyBooksService {
    public String addToShelf(Integer userID, Integer bookID, BookShelf shef);

    public Boolean deleteFromShelf(Integer userID, Integer myBookID);

    public Boolean updateShelf(Integer userID, Integer myBookID, BookShelf shelf);

    public List<MyBooksDTO> findAllMyBooks(Integer userID);

    public List<MyBooksReadDTO> findAllMyBooksRead(Integer userID);
}
