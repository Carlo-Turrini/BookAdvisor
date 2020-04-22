package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.dto.BookRankingDTO;
import com.student.book_advisor.entities.BookRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRankingRepository extends JpaRepository<BookRanking, Long> {

    @Query("SELECT new com.student.book_advisor.dto.BookRankingDTO(br.id, br.bookRank, b.id, b.titolo, b.bookCoverPath) FROM BookRanking br JOIN br.book b WHERE br.usersInfo.id = :id")
    public List<BookRankingDTO> findBookRankingByUser(@Param("userID")Long userID);
}
