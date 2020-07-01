package com.student.book_advisor.db_access.entityRepositories;

import com.student.book_advisor.db_access.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
@CrossOrigin(origins = "http://localhost:4200")
public interface GenreRepository extends JpaRepository<Genre,Integer> {

    @Query("SELECT g.genre FROM Genre g JOIN g.genreJoinBookList genBook WHERE genBook.book.id = :bookID")
    public List<String> findGenresOfBook(@Param("bookID")Integer bookID);

    public Genre findByGenre(String genre);

    @Query("SELECT g.genre FROM Genre g")
    public List<String> findAllToString();

    @Query("SELECT COUNT(g) FROM Genre g WHERE g.genre = :genre")
    public Integer countAllGenresByGenre(@Param("genre")String genre);
}
