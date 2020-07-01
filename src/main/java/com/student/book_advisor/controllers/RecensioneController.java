package com.student.book_advisor.controllers;


import com.student.book_advisor.db_access.dto.RecensioneDTO;
import com.student.book_advisor.db_access.dto.formDTOS.RecensioneFormDTO;
import com.student.book_advisor.services.RecensioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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


    @GetMapping("/libri/{id}/recensioni")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<RecensioneDTO> getAllReviewsByBook(@PathVariable("id") @Min(1) @Max(1) Integer id) {
        return recensioneService.getAllReviewsByBook(id);
    }

    @GetMapping("/utenti/{id}/recensioni")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<RecensioneDTO> getAllReviewsByUser(@PathVariable("id") @Min(1) @Max(1) Integer id) {
        return recensioneService.getAllReveiewsByUser(id);
    }

    @PostMapping("/libri/{id}/recensioni")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> addNewReview(@Valid @RequestBody RecensioneFormDTO reviewForm, BindingResult result, @PathVariable("id")Integer bookId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Set<String>> errors = new HashMap<>();
        try {
            for(FieldError fieldError: result.getFieldErrors()) {
                String code = fieldError.getCode();
                String field = fieldError.getField();
                if(code.equals("NotBlank") || code.equals("NotNull")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("required");
                }
                else if(code.equals("Min")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("min");
                }
                else if(code.equals("Max")) {
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
                recensioneService.addNewReview(reviewForm, bookId);
            }
            return errors;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') OR (#userID == authentication.principal.usersInfo.id)")
    @DeleteMapping("/utenti/{userId}/recensioni/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteReview(@PathVariable("id") Integer delReviewId, @PathVariable("userId")Integer userID, HttpServletRequest request, HttpServletResponse response) {
        try {
            recensioneService.deleteReview(delReviewId);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/recensioni/{id}/isReviewUseful")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addUsefulReview(@PathVariable("id")Integer reviewID, @RequestBody() Integer userID) {
        try {
            recensioneService.addUsefulReview(reviewID, userID);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/recensioni/{reviewID}/isReviewUseful/{userID}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeUsefulReview(@PathVariable("reviewID")Integer reviewID, @PathVariable("userID") Integer userID) {
        try {
            recensioneService.removeUsefulReview(reviewID, userID);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


}
