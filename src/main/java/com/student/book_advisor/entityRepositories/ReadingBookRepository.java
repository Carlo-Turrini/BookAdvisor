package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.entities.ReadingBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingBookRepository extends JpaRepository<ReadingBook, Long> {
}
