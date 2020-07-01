package com.student.book_advisor.db_access.entityRepositories;

import com.student.book_advisor.db_access.entities.GenreJoinBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreJoinBookRepository extends JpaRepository<GenreJoinBook, Integer> {

    @Query("SELECT gjb FROM GenreJoinBook gjb WHERE gjb.genre.genre = :genre AND gjb.book.id = :bookID")
    public GenreJoinBook findByGenreAndBookID(@Param("genre")String genre, @Param("bookID")Integer bookID);
}
