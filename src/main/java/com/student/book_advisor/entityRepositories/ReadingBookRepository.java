package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.dto.ReadingBookDTO;
import com.student.book_advisor.entities.ReadingBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadingBookRepository extends JpaRepository<ReadingBook, Long> {

    @Query("SELECT new com.student.book_advisor.dto.ReadingBookDTO(rb.id, rb.usersInfo.id, b.id, rb.pagesRead, b.pagine, b.titolo) FROM ReadingBook rb JOIN rb.book b WHERE rb.usersInfo.id = :userID")
    public List<ReadingBookDTO> findAllBooksBeingReadByUser(@Param("userID")Long userID);
}
