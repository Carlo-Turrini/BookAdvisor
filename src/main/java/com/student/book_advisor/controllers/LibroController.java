package com.student.book_advisor.controllers;


import com.student.book_advisor.constants.Constants;
import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.dto.LibroCardDTO;
import com.student.book_advisor.dto.LibroDTO;
import com.student.book_advisor.dto.formDTOS.LibroFormDTO;
import com.student.book_advisor.entities.Libro;
import com.student.book_advisor.enums.Credenziali;
import com.student.book_advisor.enums.GenereLibro;
import com.student.book_advisor.services.LibroService;
import com.student.book_advisor.services.UtenteService;
import com.student.book_advisor.session.LoggedUserDAO;
import com.student.book_advisor.session.SessionDAOFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class LibroController {

    @Autowired
    private LibroService libroService;


    @GetMapping("/libri") //Il parametro è nella query string!
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<LibroCardDTO> getAllBooksByParam(@RequestParam(name = "genere", required = false) GenereLibro genere, @RequestParam(name = "titolo", required = false)String titolo, @RequestParam(name ="titoloSaga", required = false)String titoloSaga, @RequestParam(name = "bookId", required = false)Long bookId) {
        if(titolo != null) {
            return libroService.findBooksContainingTitolo(titolo);
        }
        else if(genere != null) {
            return libroService.findAllBooksByGenre(genere);
        }
        else if(titoloSaga != null && bookId != null) {
            return libroService.findAllBooksByTitoloSagaExcludingCurrent(titoloSaga, bookId);
        }
        else return libroService.findAllBooks();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/libri/isTitleUnique")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public boolean verifyTitleUniqueness(@RequestBody()String titolo) {
        return libroService.isTitleUnique(titolo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/libri")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> newBook(@Valid @RequestBody LibroFormDTO bookForm, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Set<String>> errors = new HashMap<>();
        try {
            if(!this.libroService.isTitleUnique(bookForm.getTitolo())) {
                errors.computeIfAbsent("titolo", key -> new HashSet<>()).add("titoloTaken");
            }
            for(FieldError fieldError: result.getFieldErrors()) {
                String code = fieldError.getCode();
                String field = fieldError.getField();
                if(code.equals("NotNull") || code.equals("NotBlank")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("required");
                }
                else if(field.equals("annoPubblicazione") && code.equals("Pattern")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("pattern");
                }
                else if(field.equals("pagine") && code.equals("Min")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("min");
                }
                else if(field.equals("pagine") && code.equals("Max")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("max");
                }
                else if(field.equals("numInSaga") && code.equals("Max")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("max");
                }
                else if(field.equals("titolo") && code.equals("Size")) {
                    if(bookForm.getTitolo().length() < 1) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                    }
                    else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                }
                else if(field.equals("autori") && code.equals("Size")) {
                    if(bookForm.getAutori().length() < 2) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                    }
                    else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                }
                else if(field.equals("sinossi") && code.equals("Size")) {
                    if(bookForm.getSinossi().length() < 1) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                    }
                    else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                }
                else if(field.equals("titoloSaga") && code.equals("Size")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                }
            }
            if(errors.isEmpty()) {
                Libro newBook = new Libro();
                newBook.setAnnoPubblicazione(bookForm.getAnnoPubblicazione());
                newBook.setPagine(bookForm.getPagine());
                newBook.setAutori(bookForm.getAutori());
                newBook.setSinossi(bookForm.getSinossi());
                newBook.setTitolo(bookForm.getTitolo());
                newBook.setGenere(bookForm.getGenere());
                newBook.setSaga(bookForm.getSaga());
                if(bookForm.getSaga()) {
                    newBook.setTitoloSaga(bookForm.getTitoloSaga());
                    newBook.setNumInSaga(bookForm.getNumInSaga());
                }
                libroService.newBook(newBook);
            }
            return errors;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/libri/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public LibroDTO getBook(@PathVariable("id") @Min(1) @Max(1) Long id) {
        return libroService.findBookDTOById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/libri/{id}/foto_copertina")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String updateBooksCoverPhoto(@RequestParam("copertina") MultipartFile coverPhoto, @PathVariable("id")Long bookId, HttpServletRequest request, HttpServletResponse response) {
        try {
            Libro book = libroService.findBookById(bookId);
            if(book != null) {
                return libroService.updateBooksCoverPhoto(coverPhoto, book);
            }
            else throw new ApplicationException("Libro inesistente!");
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/libri/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> updateBook(@Valid @RequestBody LibroFormDTO bookForm, BindingResult result, @PathVariable("id")Long bookId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Set<String>> errors = new HashMap<>();
        try {
            Libro book = libroService.findBookById(bookId);
            if(book != null) {
                if (!bookForm.getTitolo().equals(book.getTitolo()) && !this.libroService.isTitleUnique(bookForm.getTitolo())) {
                    errors.computeIfAbsent("titolo", key -> new HashSet<>()).add("titoloTaken");
                }
                for (FieldError fieldError : result.getFieldErrors()) {
                    String code = fieldError.getCode();
                    String field = fieldError.getField();
                    if (code.equals("NotNull") || code.equals("NotBlank")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("required");
                    } else if (field.equals("annoPubblicazione") && code.equals("Pattern")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("pattern");
                    } else if (field.equals("pagine") && code.equals("Min")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("min");
                    } else if (field.equals("pagine") && code.equals("Max")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("max");
                    } else if (field.equals("numInSaga") && code.equals("Max")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("max");
                    } else if (field.equals("titolo") && code.equals("Size")) {
                        if (bookForm.getTitolo().length() < 1) {
                            errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                        } else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                    } else if (field.equals("autori") && code.equals("Size")) {
                        if (bookForm.getAutori().length() < 2) {
                            errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                        } else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                    } else if (field.equals("sinossi") && code.equals("Size")) {
                        if (bookForm.getSinossi().length() < 1) {
                            errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                        } else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                    } else if (field.equals("titoloSaga") && code.equals("Size")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                    }
                }
                if (errors.isEmpty()) {
                    book.setAnnoPubblicazione(bookForm.getAnnoPubblicazione());
                    book.setPagine(bookForm.getPagine());
                    book.setAutori(bookForm.getAutori());
                    book.setSinossi(bookForm.getSinossi());
                    book.setTitolo(bookForm.getTitolo());
                    book.setGenere(bookForm.getGenere());
                    book.setSaga(bookForm.getSaga());
                    if (bookForm.getSaga()) {
                        book.setTitoloSaga(bookForm.getTitoloSaga());
                        book.setNumInSaga(bookForm.getNumInSaga());
                    }
                    libroService.updateBook(book);
                }
                return errors;
            }
            else throw new ApplicationException("Libro inesistente!");
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/libri/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteBook(@PathVariable("id") @Min(1) @Max(1) Long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            libroService.deleteBook(id);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/libri/{id}/overallRating")
    @CrossOrigin(origins = "http://localhost:4200")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Double getBookOverallRating(@PathVariable("id")Long bookId) {
        return libroService.getBookOverallRating(bookId);
    }
}
