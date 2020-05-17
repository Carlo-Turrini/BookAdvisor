package com.student.book_advisor.services;

import com.student.book_advisor.dto.BookRankingDTO;

import java.util.List;

public interface BookRankingService {
    public List<BookRankingDTO> findUsersBookRank(Integer userID);

    public List<BookRankingDTO> addBookToBookRank(Integer userID, Integer bookID, Integer bookRank);

    public List<BookRankingDTO> removeBookFromBookRank(Integer userID, Integer bookRankID);
}
