package com.student.book_advisor.data_persistency.repositories;

import com.student.book_advisor.data_persistency.model.dto.LibroCardDTO;
import com.student.book_advisor.data_persistency.model.dto.LibroDTO;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
@CrossOrigin(origins = "http://localhost:4200")
public interface LibroRepository extends JpaRepository<Libro, Integer> {
    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.LibroCardDTO(l.id, l.titolo, AVG(r.rating) ) FROM Libro l JOIN l.genreJoinBookList genres LEFT JOIN Recensione r ON (r.libro.id = l.id)  WHERE genres.genre.genre = :genere GROUP BY l.id, l.titolo ORDER BY AVG(r.rating) DESC ")
    public List<LibroCardDTO> findLibriByGenere(@Param("genere")String genere);

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.LibroCardDTO(l.id, l.titolo, AVG(r.rating)) FROM Libro l LEFT JOIN Recensione r ON (r.libro.id = l.id) GROUP BY l.id, l.titolo ORDER BY AVG(r.rating) DESC ")
    public List<LibroCardDTO> findAllBooks();

    @Query("SELECT l.bookCoverPath FROM Libro l WHERE l.id = :bookId")
    public String findBookCoverPath(@Param("bookId")Integer id);

    @Query("SELECT count(l) FROM Libro l WHERE l.titolo = :titolo")
    public Integer countAllByTitolo(@Param("titolo")String titolo);

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.LibroCardDTO(l.id, l.titolo, AVG(r.rating)) FROM Libro l LEFT JOIN Recensione r ON (r.libro.id = l.id) WHERE l.titolo LIKE CONCAT('%',:titolo,'%') GROUP BY l.id, l.titolo ORDER BY AVG(r.rating) DESC")
    public List<LibroCardDTO> findAllBooksContainingTitolo(@Param("titolo")String titolo);

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.LibroDTO(l.id, l.titolo, l.annoPubblicazione, l.pagine, l.sinossi, s.sagaTitle, s.numberInSaga, AVG(r.rating), AVG(r.writingQualityRating), AVG(r.pageTurnerRating), AVG(r.pageTurnerRating)) FROM Libro l LEFT JOIN Saga s ON (s.book.id = l.id) LEFT JOIN Recensione r ON (r.libro.id = l.id) WHERE l.id = :id GROUP BY l.id, l.titolo, l.annoPubblicazione, l.pagine, l.sinossi, s.sagaTitle, s.numberInSaga")
    public LibroDTO getBookById(@Param("id")Integer bookId);

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.LibroCardDTO(l.id, l.titolo, AVG(r.rating)) FROM Libro l JOIN l.saga s LEFT JOIN Recensione r ON (r.libro.id = l.id) WHERE s.sagaTitle = :titoloSaga AND l.id <> :bookId GROUP BY l.id, l.titolo")
    public List<LibroCardDTO> findAllBooksByTitoloSagaExcludingCurrent(@Param("titoloSaga")String titoloSaga, @Param("bookId")Integer id);

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.LibroCardDTO(l.id, l.titolo, AVG(r.rating)) FROM Libro l JOIN l.authorJoinBookList  ajbl JOIN ajbl.author a LEFT JOIN Recensione r ON (r.libro.id = l.id) WHERE a.authorsFullname = :authorsFullname GROUP BY l.id, l.titolo")
    public List<LibroCardDTO> findAllBooksByAuthor(@Param("authorsFullname")String authorsFullname);

    @Query("SELECT b FROM Libro b JOIN MyBooks mb ON (mb.book.id = b.id) WHERE mb.id = :myBooksID")
    public Libro getByMyBooksID(@Param("myBooksID")Integer myBooksID);


}
