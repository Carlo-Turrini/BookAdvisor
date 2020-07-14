package com.student.book_advisor.data_persistency.repositories;

import com.student.book_advisor.data_persistency.model.entities.Author;
import com.student.book_advisor.data_persistency.model.entities.AuthorJoinBook;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthorJoinBookRepository extends JpaRepository<AuthorJoinBook, Integer> {

    public AuthorJoinBook findByAuthorAndBook(Author author, Libro book);
}
