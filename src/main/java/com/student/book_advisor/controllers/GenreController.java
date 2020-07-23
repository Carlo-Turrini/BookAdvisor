package com.student.book_advisor.controllers;


import com.student.book_advisor.data_persistency.model.dto.formDTOS.GenreFormDTO;
import com.student.book_advisor.services.GenreService;
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
public class GenreController {
    @Autowired
    private GenreService genreService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/genres")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> addGenre(@Valid @RequestBody() GenreFormDTO genreForm, BindingResult result) {
        Map<String, Set<String>> errors = new HashMap<String, Set<String>>();
        try {
            if(!genreService.isGenreUnique(genreForm.getGenre())) {
                errors.computeIfAbsent("genre", key -> new HashSet<String>()).add("genreTaken");
            }
            for(FieldError fieldError : result.getFieldErrors()) {
                String field = fieldError.getField();
                String code = fieldError.getCode();
                if(code.equals("NotBlank")) {
                    errors.computeIfAbsent("genre", key -> new HashSet<String>()).add("required");
                }
                if(code.equals("Size")) {
                    if(genreForm.getGenre().length() > 20) {
                        errors.computeIfAbsent("genre", key -> new HashSet<String>()).add("maxlength");
                    }
                    else errors.computeIfAbsent("genre", key -> new HashSet<String>()).add("minlength");
                }
            }
            if(errors.isEmpty()) {
                genreService.addGenre(genreForm.getGenre());
            }
            return errors;
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore aggiunta nuovo genere", e);
        }
    }

    @GetMapping("/genres")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<String> getAllGenres() {
        try {
            return genreService.getAllGenres();
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore caricamento generi", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/genres/isGenreUnique")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Boolean isGenreUnique(@RequestBody() String genre) {
        return genreService.isGenreUnique(genre);
    }
}
