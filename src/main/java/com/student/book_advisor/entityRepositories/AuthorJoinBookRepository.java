package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.entities.Author;
import com.student.book_advisor.entities.AuthorJoinBook;
import com.student.book_advisor.entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthorJoinBookRepository extends JpaRepository<AuthorJoinBook, Integer> {

    public AuthorJoinBook findByAuthorAndBook(Author author, Libro book);
}
