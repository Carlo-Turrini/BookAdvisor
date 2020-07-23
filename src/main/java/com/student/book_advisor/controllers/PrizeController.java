package com.student.book_advisor.controllers;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.PrizeDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.PrizeFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.services.LibroService;
import com.student.book_advisor.services.PrizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PrizeController {
    @Autowired
    private LibroService libroService;
    @Autowired
    private PrizeService prizeService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/libri/{id}/prizes")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> addPrize(@Valid @RequestBody PrizeFormDTO prizeForm, BindingResult result, @PathVariable("id")Integer bookID) {
        Map<String, Set<String>> errors = new HashMap<String, Set<String>>();
        try {
            Libro book = libroService.findBookById(bookID);
            if(book != null) {
                if(prizeService.isPrizeAlreadyAssignedToBook(prizeForm.getPrizeName(), bookID)) {
                    errors.computeIfAbsent("prizeName", key -> new HashSet<String>()).add("prizeAssigned");
                }
                for(FieldError fieldError : result.getFieldErrors()) {
                    String code = fieldError.getCode();
                    String field = fieldError.getField();
                    if(code.equals("NotBlank") || code.equals("NotNull")) {
                        errors.computeIfAbsent(field, key -> new HashSet<String>()).add("required");
                    }
                    else if(code.equals("Min")) {
                        errors.computeIfAbsent(field, key -> new HashSet<String>()).add("min");
                    }
                    else if(code.equals("Max")) {
                        errors.computeIfAbsent(field, key -> new HashSet<String>()).add("max");
                    }
                    else if(code.equals("Size")) {
                        String err;
                        if(prizeForm.getPrizeName().length() > 64) {
                            err = "maxlength";
                        }
                        else err = "minlength";
                        errors.computeIfAbsent(field, key -> new HashSet<String>()).add(err);
                    }
                }
                if(errors.isEmpty()) {
                    prizeService.addPrize(prizeForm, bookID);
                }
                return errors;
            }
            else throw new ApplicationException("Book not found");
        }
        catch (ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore nuovo premio", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/libri/{id}/prizes/{prizeID}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deletePrize(@PathVariable("id")Integer bookID, @PathVariable("prizeID")Integer prizeID) {
        try {
            prizeService.deletePrize(prizeID, bookID);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore cancellazione premio", e);
        }
    }

    @GetMapping("/libri/{id}/prizes")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<PrizeDTO> getPrizesOfBook(@PathVariable("id")Integer bookID) {
        return prizeService.getAllPrizesOfBook(bookID);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/libri/{id}/prizes/{prizeName}")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public PrizeDTO getPrizeOfBookFromName(@PathVariable("id")Integer bookID, @PathVariable("prizeName")String prizeName) {
        return prizeService.getPrizeOfBookFromName(bookID, prizeName);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/libri/{id}/prizes/isPrizeAssigned")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Boolean isPrizeAlreadyAssignedToBook(@PathVariable("id")Integer bookID, @RequestBody()String prizeName) {
        return prizeService.isPrizeAlreadyAssignedToBook(prizeName, bookID);
    }
}
