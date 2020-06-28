package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.entities.Genre;
import com.student.book_advisor.entities.GenreJoinBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreJoinBookRepository extends JpaRepository<GenreJoinBook, Integer> {

    public GenreJoinBook findByGenre(Genre genre);

    @Query("SELECT gjb FROM GenreJoinBook gjb WHERE gjb.genre.genre = :genre AND gjb.book.id = :bookID")
    public GenreJoinBook findByGenreAndBookID(@Param("genre")String genre, @Param("bookID")Integer bookID);
}
