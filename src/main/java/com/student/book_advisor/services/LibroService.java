package com.student.book_advisor.services;

import com.student.book_advisor.dto.LibroCardDTO;
import com.student.book_advisor.dto.LibroDTO;
import com.student.book_advisor.dto.PrizeDTO;
import com.student.book_advisor.dto.auxiliaryDTOs.OverallRatingsForBook;
import com.student.book_advisor.dto.formDTOS.LibroFormDTO;
import com.student.book_advisor.entities.Libro;
import com.student.book_advisor.enums.GenereLibro;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LibroService {

    public List<LibroCardDTO> findAllBooks();

    public List<LibroCardDTO> findAllBooksByGenre(GenereLibro genere);

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
