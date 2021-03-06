package com.student.book_advisor.controllers;


import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.LibroCardDTO;
import com.student.book_advisor.data_persistency.model.dto.LibroDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.OverallRatingsForBook;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.LibroFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.services.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/api")
public class LibroController {

    @Autowired
    private LibroService libroService;


    @GetMapping("/libri") //Il parametro è nella query string!
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<LibroCardDTO> getAllBooksByParam(@RequestParam(name = "genere", required = false) String genere, @RequestParam(name = "titolo", required = false)String titolo, @RequestParam(name ="titoloSaga", required = false)String titoloSaga, @RequestParam(name = "bookId", required = false)Integer bookId, @RequestParam(name = "author", required = false)String authorsFullname) {
        if(titolo != null) {
            return libroService.findBooksContainingTitolo(titolo);
        }
        else if(genere != null) {
            return libroService.findAllBooksByGenre(genere);
        }
        else if(titoloSaga != null && bookId != null) {
            return libroService.findAllBooksByTitoloSagaExcludingCurrent(titoloSaga, bookId);
        }
        else if(authorsFullname != null) {
            return libroService.findAllBooksByAuthor(authorsFullname);
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
    public Map<String, Set<String>> newBook(@Valid @RequestBody LibroFormDTO bookForm, BindingResult result) {
        Map<String, Set<String>> errors = new HashMap<>();
        try {
            if(!this.libroService.isTitleUnique(bookForm.getTitolo())) {
                errors.computeIfAbsent("titolo", key -> new HashSet<>()).add("titoloTaken");
            }
            for(FieldError fieldError: result.getFieldErrors()) {
                String code = fieldError.getCode();
                String field = fieldError.getField();
                if(code.equals("NotNull") || code.equals("NotBlank") || code.equals("NotEmpty")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("required");
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
                else if(field.equals("sinossi") && code.equals("Size")) {
                    if(bookForm.getSinossi().length() < 1) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                    }
                    else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                }
                else if(field.equals("titoloSaga") && code.equals("Size")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                }
                else if(field.equals("annoPubblicazione") && code.equals("Min")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("min");
                }
            }
            int year = Calendar.getInstance().get(Calendar.YEAR);
            if(bookForm.getAnnoPubblicazione()>year) {
                errors.computeIfAbsent("annoPubblicazione", key -> new HashSet<>()).add("max");
            }
            if(errors.isEmpty()) {
                libroService.newBook(bookForm);
            }
            return errors;
        }
        catch(ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore aggiunta nuovo libro", e);
        }
    }

    @GetMapping("/libri/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public LibroDTO getBook(@PathVariable("id") @Min(1) @Max(1) Integer id) {
        return libroService.findBookDTOById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/libri/{id}/foto_copertina")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String updateBooksCoverPhoto(@RequestPart("copertina") MultipartFile coverPhoto, @PathVariable("id")Integer bookId, HttpServletRequest request, HttpServletResponse response) {
        try {
            Libro book = libroService.findBookById(bookId);
            if(book != null) {
                return libroService.updateBooksCoverPhoto(coverPhoto, book);
            }
            else throw new ApplicationException("Libro inesistente!");
        }
        catch(ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore aggiornamento copertina libro", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/libri/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> updateBook(@Valid @RequestBody LibroFormDTO bookForm, BindingResult result, @PathVariable("id")Integer bookId, HttpServletRequest request, HttpServletResponse response) {
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
                    if (code.equals("NotNull") || code.equals("NotBlank") || code.equals("NotEmpty")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("required");
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
                    } else if (field.equals("sinossi") && code.equals("Size")) {
                        if (bookForm.getSinossi().length() < 1) {
                            errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                        } else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                    } else if (field.equals("titoloSaga") && code.equals("Size")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                    }
                    else if(field.equals("annoPubblicazione") && code.equals("Min")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("min");
                    }
                }
                int year = Calendar.getInstance().get(Calendar.YEAR);
                if(bookForm.getAnnoPubblicazione()>year) {
                    errors.computeIfAbsent("annoPubblicazione", key -> new HashSet<>()).add("max");
                }
                if (errors.isEmpty()) {
                    libroService.updateBook(bookForm, bookId);
                }
                return errors;
            }
            else throw new ApplicationException("Libro inesistente!");
        }
        catch(ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore aggiornamento libro", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/libri/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteBook(@PathVariable("id") @Min(1) @Max(1) Integer id, HttpServletRequest request, HttpServletResponse response) {
        try {
            libroService.deleteBook(id);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/libri/{id}/overallRatings")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public OverallRatingsForBook getBookOverallRatiings(@PathVariable("id")Integer bookID) {
        return libroService.getBookOverallRatings(bookID);
    }


}
