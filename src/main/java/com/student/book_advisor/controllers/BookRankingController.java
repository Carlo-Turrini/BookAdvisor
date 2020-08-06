package com.student.book_advisor.controllers;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.BookRankingDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.BookForRankDTO;
import com.student.book_advisor.services.BookRankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/api")
public class BookRankingController {
    @Autowired
    private BookRankingService bookRankingService;

    @GetMapping("/utenti/{id}/bookRank")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<BookRankingDTO> getUsersBookRanking(@PathVariable("id")Integer userID) {
        try {
            return bookRankingService.findUsersBookRank(userID);
        }
        catch (ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore nel caricamento del ranking", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') OR (#userID == authentication.principal.usersInfo.id)")
    @PostMapping("/utenti/{id}/bookRank")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<BookRankingDTO> addBookRank(@PathVariable("id")Integer userID, @RequestBody() BookForRankDTO bookForRankDTO) {
        try {
            return bookRankingService.addBookToBookRank(userID, bookForRankDTO.getMyBookID(), bookForRankDTO.getRank());
        }
        catch (ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore nell'aggiunta al ranking", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') OR (#userID == authentication.principal.usersInfo.id)")
    @DeleteMapping("/utenti/{id}/bookRank/{rankID}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<BookRankingDTO> removeBookRank(@PathVariable("id")Integer userID, @PathVariable("rankID")Integer rankID) {
        try {
            return bookRankingService.removeBookFromBookRank(userID, rankID);
        }
        catch (ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore nella rimozione dal ranking", e);
        }
    }
}
