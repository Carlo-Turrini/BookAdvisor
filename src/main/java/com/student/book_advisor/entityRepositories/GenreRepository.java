package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
@CrossOrigin(origins = "http://localhost:4200")
public interface GenreRepository extends JpaRepository<Genre,Long> {

    @Query("SELECT g.genre FROM Genre g JOIN g.genreJoinBookList genBook WHERE genBook.book.id = :bookID")
    public List<String> findGenresOfBook(@Param("bookID")Long bookID);
}
