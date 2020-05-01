package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.entities.Genre;
import com.student.book_advisor.entities.GenreJoinBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreJoinBookRepository extends JpaRepository<GenreJoinBook, Long> {

    public GenreJoinBook findByGenre(Genre genre);
}
