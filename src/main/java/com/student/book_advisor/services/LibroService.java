package com.student.book_advisor.services;

import com.student.book_advisor.dto.LibroCardDTO;
import com.student.book_advisor.dto.LibroDTO;
import com.student.book_advisor.entities.Libro;
import com.student.book_advisor.enums.GenereLibro;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LibroService {

    public List<LibroCardDTO> findAllBooks();

    public List<LibroCardDTO> findAllBooksByGenre(GenereLibro genere);

    public Libro findBookById(Long id);

    public LibroDTO findBookByTitolo(String titolo);

    public LibroDTO findBookDTOById(Long id);

    public Libro newBook(Libro newBook);

    public Libro updateBook(Libro updatedBook);

    public void deleteBook(Long id);

    public boolean isTitleUnique(String titolo);

    public String updateBooksCoverPhoto(MultipartFile coverPhoto, Libro book);

    public List<LibroCardDTO> findBooksContainingTitolo(String titolo);

    public List<LibroCardDTO> findAllBooksByTitoloSagaExcludingCurrent(String titoloSaga, Long bookId);

    public Double getBookOverallRating(Long id);
}
