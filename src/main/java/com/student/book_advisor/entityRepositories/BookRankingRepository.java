package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.dto.BookRankingDTO;
import com.student.book_advisor.entities.BookRanking;
import com.student.book_advisor.entities.MyBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRankingRepository extends JpaRepository<BookRanking, Long> {

    @Query("SELECT new com.student.book_advisor.dto.BookRankingDTO(br.id, br.bookRank, b.id, b.titolo) FROM BookRanking br JOIN MyBooks mb ON (br.myBooks.id = mb.id) JOIN mb.book b WHERE mb.usersInfo.id = :id")
    public List<BookRankingDTO> findBookRankingByUser(@Param("userID")Long userID);

    @Query("SELECT br FROM BookRanking  br JOIN MyBooks mb ON (br.myBooks.id = mb.id) WHERE mb.usersInfo.id = :userID")
    public List<BookRanking> findAllByUserID(@Param("userID")Long userID);

    @Query("SELECT br.myBooks.id FROM BookRanking br JOIN MyBooks mb ON (br.myBooks.id = mb.id) WHERE mb.usersInfo.id = :userID")
    public List<Long> findAllMyBookIDsInRank(@Param("userID")Long userID);

    @Query("SELECT mb.usersInfo.id FROM BookRanking br JOIN MyBooks mb ON (br.myBooks.id = mb.id) WHERE br.id = :rankID")
    public Long getUsersIDFromBookRanking(@Param("rankID")Long rankID);

    public BookRanking getBookRankingByMyBooks(MyBooks myBooks);
}
