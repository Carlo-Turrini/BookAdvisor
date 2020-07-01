package com.student.book_advisor.services;

import com.student.book_advisor.db_access.dto.LibroCardDTO;
import com.student.book_advisor.db_access.dto.LibroDTO;
import com.student.book_advisor.db_access.dto.PrizeDTO;
import com.student.book_advisor.db_access.dto.auxiliaryDTOs.OverallRatingsForBook;
import com.student.book_advisor.db_access.dto.formDTOS.LibroFormDTO;
import com.student.book_advisor.db_access.entities.Libro;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LibroService {

    public List<LibroCardDTO> findAllBooks();

    public List<LibroCardDTO> findAllBooksByGenre(String genere);

    public Libro findBookById(Integer id);

    public LibroDTO findBookByTitolo(String titolo);

    public LibroDTO findBookDTOById(Integer id);

    public Libro newBook(LibroFormDTO libroForm);

    public Libro updateBook(LibroFormDTO libroForm, Integer bookID);

    public void deleteBook(Integer id);

    public boolean isTitleUnique(String titolo);

    public String updateBooksCoverPhoto(MultipartFile coverPhoto, Libro book);

    public List<LibroCardDTO> findBooksContainingTitolo(String titolo);

    public List<LibroCardDTO> findAllBooksByTitoloSagaExcludingCurrent(String titoloSaga, Integer bookId);

    public Double getBookOverallRating(Integer id);

    public List<PrizeDTO> addPrize(PrizeDTO prize, Integer bookID);

    public List<PrizeDTO> removePrize(Integer prizeID, Integer bookID);

    public OverallRatingsForBook getBookOverallRatings(Integer bookID);

    public List<LibroCardDTO> findAllBooksByAuthor(String authorsFullname);
}
