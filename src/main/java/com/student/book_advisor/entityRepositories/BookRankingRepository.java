package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.dto.BookRankingDTO;
import com.student.book_advisor.dto.auxiliaryDTOs.BookRankRemovalInfoDTO;
import com.student.book_advisor.entities.BookRanking;
import com.student.book_advisor.entities.MyBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRankingRepository extends JpaRepository<BookRanking, Integer> {

    @Query("SELECT new com.student.book_advisor.dto.BookRankingDTO(br.id, br.bookRank, b.id, b.titolo) FROM BookRanking br JOIN MyBooks mb ON (br.myBooks.id = mb.id) JOIN mb.book b WHERE mb.usersInfo.id = :userID ORDER BY br.bookRank ASC")
    public List<BookRankingDTO> findBookRankingByUser(@Param("userID")Integer userID);

    @Query("SELECT br FROM BookRanking  br JOIN MyBooks mb ON (br.myBooks.id = mb.id) WHERE mb.usersInfo.id = :userID ORDER BY br.bookRank ASC")
    public List<BookRanking> findAllByUserID(@Param("userID")Integer userID);

    @Query("SELECT br.myBooks.id FROM BookRanking br JOIN MyBooks mb ON (br.myBooks.id = mb.id) WHERE mb.usersInfo.id = :userID")
    public List<Integer> findAllMyBookIDsInRank(@Param("userID")Integer userID);

    @Query("SELECT mb.usersInfo.id FROM BookRanking br JOIN MyBooks mb ON (br.myBooks.id = mb.id) WHERE br.id = :rankID")
    public Integer getUsersIDFromBookRanking(@Param("rankID")Integer rankID);

    @Query("SELECT br FROM BookRanking br JOIN MyBooks mb ON (br.myBooks.id = mb.id) WHERE mb.id = :id")
    public BookRanking getBookRankingByMyBooksID(@Param("id")Integer myBookID);

    public BookRanking getBookRankingByMyBooks(MyBooks myBooks);

    @Query("SELECT new com.student.book_advisor.dto.auxiliaryDTOs.BookRankRemovalInfoDTO(mb.usersInfo.id, br.id) FROM BookRanking br JOIN MyBooks mb ON (br.myBooks.id = mb.id) WHERE mb.book.id = :bookID")
    public List<BookRankRemovalInfoDTO> getAllBookRanksByBookID(@Param("bookID")Integer bookID);

}
