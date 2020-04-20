package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.entities.BookRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRankingRepository extends JpaRepository<BookRanking, Long> {
}
