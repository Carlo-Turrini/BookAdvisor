package com.student.book_advisor.controllers;


import com.student.book_advisor.constants.Constants;
import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.dto.RecensioneDTO;
import com.student.book_advisor.dto.formDTOS.RecensioneFormDTO;
import com.student.book_advisor.entities.Libro;
import com.student.book_advisor.entities.Recensione;
import com.student.book_advisor.entities.Utente;
import com.student.book_advisor.enums.Credenziali;
import com.student.book_advisor.security.AuthUserPrincipal;
import com.student.book_advisor.services.LibroService;
import com.student.book_advisor.services.RecensioneService;
import com.student.book_advisor.services.UtenteService;
import com.student.book_advisor.session.LoggedUserDAO;
import com.student.book_advisor.session.SessionDAOFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class RecensioneController {

    @Autowired
    private RecensioneService recensioneService;

    @Autowired
    private LibroService libroService;


    @GetMapping("/libri/{id}/recensioni")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<RecensioneDTO> getAllReviewsByBook(@PathVariable("id") @Min(1) @Max(1) Long id) {
        return recensioneService.getAllReviewsByBook(id);
    }

    @GetMapping("/utenti/{id}/recensioni")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<RecensioneDTO> getAllReviewsByUser(@PathVariable("id") @Min(1) @Max(1) Long id) {
        return recensioneService.getAllReveiewsByUser(id);
    }

    @PostMapping("/libri/{id}/recensioni")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> addNewReview(@Valid @RequestBody RecensioneFormDTO reviewForm, BindingResult result, @PathVariable("id")Long bookId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Set<String>> errors = new HashMap<>();
        try {
            for(FieldError fieldError: result.getFieldErrors()) {
                String code = fieldError.getCode();
                String field = fieldError.getField();
                if(code.equals("NotBlank") || code.equals("NotNull")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("required");
                }
                else if(code.equals("Min") && field.equals("rating")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("min");
                }
                else if(code.equals("Max") && field.equals("rating")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("max");
                }
                else if(code.equals("Size") && field.equals("testo")) {
                    if(reviewForm.getTesto().length() < 1) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                    }
                    else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                }
            }
            if(errors.isEmpty()) {
                Recensione newReview = new Recensione();
                Libro bookToReview = libroService.findBookById(bookId);
                if (bookToReview != null) {
                    newReview.setLibro(bookToReview);
                    newReview.setUsersInfo(((AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsersInfo());
                    newReview.setRating(reviewForm.getRating());
                    newReview.setTesto(reviewForm.getTesto());
                    recensioneService.addNewReview(newReview);
                } else throw new ApplicationException("Libro inesistente!");
            }
            return errors;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') OR isUsersReview(#id)")
    @DeleteMapping(value = {"/libri/{bookId}/recensioni/{id}", "/utenti/{userId}/recensioni/{id}"})
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteReview(@PathVariable("id") Long delReviewId, HttpServletRequest request, HttpServletResponse response) {
        try {
            recensioneService.deleteReview(delReviewId);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


}
