package com.student.book_advisor.db_access.entityRepositories;

import com.student.book_advisor.db_access.entities.Author;
import com.student.book_advisor.db_access.entities.AuthorJoinBook;
import com.student.book_advisor.db_access.entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthorJoinBookRepository extends JpaRepository<AuthorJoinBook, Integer> {

    public AuthorJoinBook findByAuthorAndBook(Author author, Libro book);
}
