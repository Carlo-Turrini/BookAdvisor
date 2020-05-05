package com.student.book_advisor.services;

import com.student.book_advisor.dto.BookRankingDTO;

import java.util.List;

public interface BookRankingService {
    public List<BookRankingDTO> findUsersBookRank(Long userID);

    public List<BookRankingDTO> addBookToBookRank(Long userID, Long bookID, Integer bookRank);

    public List<BookRankingDTO> removeBookFromBookRank(Long userID, Long bookRankID);
}
