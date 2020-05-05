package com.student.book_advisor.controllers;


import com.student.book_advisor.services.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class GenreController {
    @Autowired
    private GenreService genreService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/genres")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> addGenre(@RequestParam("genre")String genre) {
        try {
            return genreService.addGenre(genre);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/genres")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<String> getAllGenres() {
        try {
            return genreService.getAllGenres();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
