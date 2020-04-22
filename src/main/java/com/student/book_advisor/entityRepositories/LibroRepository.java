package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.dto.LibroCardDTO;
import com.student.book_advisor.dto.LibroDTO;
import com.student.book_advisor.entities.Libro;
import com.student.book_advisor.enums.GenereLibro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
@CrossOrigin(origins = "http://localhost:4200")
//DA AGGIORNARE
public interface LibroRepository extends JpaRepository<Libro, Long> {
    //Aggiungi clausola OrderBy AVG on rating recensioni libro!
    @Query("SELECT DISTINCT new com.student.book_advisor.dto.LibroCardDTO(l.id, l.titolo, l.autori, l.genere) FROM Libro l  WHERE l.genere = :genere")
    public List<LibroCardDTO> findLibriByGenere(@Param("genere") GenereLibro genere);

    @Query("SELECT DISTINCT new com.student.book_advisor.dto.LibroCardDTO(l.id, l.titolo, l.autori, l.genere) FROM Libro l")
    public List<LibroCardDTO> findAllBooks();

    @Query("SELECT l.bookCoverPath FROM Libro l WHERE l.id = :bookId")
    public String findBookCoverPath(@Param("bookId")Long id);

    @Query("SELECT count(l) FROM Libro l WHERE l.titolo = :titolo")
    public Integer countAllByTitolo(@Param("titolo")String titolo);

    @Query("SELECT new com.student.book_advisor.dto.LibroDTO(l.id, l.titolo, l.autori, l.annoPubblicazione, l.pagine, l.genere, l.sinossi, l.saga, l.titoloSaga, l.numInSaga) FROM Libro l WHERE l.titolo = :titolo")
    public LibroDTO findByTitolo(String titolo);

    @Query("SELECT DISTINCT new com.student.book_advisor.dto.LibroCardDTO(l.id, l.titolo, l.autori, l.genere) FROM Libro l WHERE l.titolo LIKE CONCAT('%',:titolo,'%')")
    public List<LibroCardDTO> findAllBooksContainingTitolo(@Param("titolo")String titolo);

    @Query("SELECT new com.student.book_advisor.dto.LibroDTO(l.id, l.titolo, l.autori, l.annoPubblicazione, l.pagine, l.genere, l.sinossi, l.saga, l.titoloSaga, l.numInSaga) FROM Libro l WHERE l.id = :id")
    public LibroDTO getBookById(@Param("id")Long bookId);

    @Query("SELECT new com.student.book_advisor.dto.LibroCardDTO(l.id, l.titolo, l.autori, l.genere) FROM Libro l WHERE l.titoloSaga = :titoloSaga AND l.id <> :bookId ORDER BY l.numInSaga")
    public List<LibroCardDTO> findAllBooksByTitoloSagaExcludingCurrent(@Param("titoloSaga")String titoloSaga, @Param("bookId")Long id);
}
