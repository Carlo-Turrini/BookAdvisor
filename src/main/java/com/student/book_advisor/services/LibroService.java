package com.student.book_advisor.services;

import com.student.book_advisor.data_persistency.model.dto.LibroCardDTO;
import com.student.book_advisor.data_persistency.model.dto.LibroDTO;
import com.student.book_advisor.data_persistency.model.dto.PrizeDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.OverallRatingsForBook;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.LibroFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LibroService {

    public List<LibroCardDTO> findAllBooks();

    public List<LibroCardDTO> findAllBooksByGenre(String genere);

    public Libro findBookById(Integer id);

    public LibroDTO findBookDTOById(Integer id);

    public Libro newBook(LibroFormDTO libroForm);

    public Libro updateBook(LibroFormDTO libroForm, Integer bookID);

    public void deleteBook(Integer id);

    public boolean isTitleUnique(String titolo);

    public String updateBooksCoverPhoto(MultipartFile coverPhoto, Libro book);

    public List<LibroCardDTO> findBooksContainingTitolo(String titolo);

    public List<LibroCardDTO> findAllBooksByTitoloSagaExcludingCurrent(String titoloSaga, Integer bookId);

    public OverallRatingsForBook getBookOverallRatings(Integer bookID);

    public List<LibroCardDTO> findAllBooksByAuthor(String authorsFullname);
}
