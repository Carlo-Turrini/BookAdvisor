package com.student.book_advisor.controllers;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.MyBooksDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.MyBooksReadDTO;
import com.student.book_advisor.enums.BookShelf;
import com.student.book_advisor.services.MyBooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/api")
public class MyBooksController {
    @Autowired
    private MyBooksService myBooksService;

    @PreAuthorize("hasRole('ADMIN') OR (#userID == authentication.principal.usersInfo.id)")
    @DeleteMapping("/utenti/{id}/myBooks/{myBookID}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean deleteBookFromMyBooks(@PathVariable("id") @Min(1) @Max(1) Integer userID, @PathVariable("myBookID") @Min(1) @Max(1) Integer myBookID, HttpServletRequest request, HttpServletResponse response) {
        try {
            //Il boolean ritornato identifica se è stato modificato il rank oppure no.
            return myBooksService.deleteFromShelf(userID, myBookID);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore cancellazione libro da MyBooks", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') OR (#userID == authentication.principal.usersInfo.id)")
    @PutMapping("/utenti/{id}/myBooks/{bookID}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean updateBookFromMyBooks(@PathVariable("id") @Min(1) @Max(1) Integer userID, @PathVariable("bookID") @Min(1) @Max(1) Integer bookID, @RequestBody() String shelf) {
        try {
            return myBooksService.updateShelf(userID, bookID, BookShelf.valueOf(shelf));
        }
        catch(ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore aggiornamento MyBooks", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') OR (#userID == authentication.principal.usersInfo.id)")
    @GetMapping("/utenti/{id}/myBooks/booksReadNotInRank")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<MyBooksReadDTO> getAllMyBooksReadNotInRank(@PathVariable("id")Integer userID) {
        try {
            return myBooksService.findAllMyBooksRead(userID);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore caricamento MyBooks letti non presenti nel ranking", e);
        }
    }

    @GetMapping("/utenti/{id}/myBooks")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<MyBooksDTO> getAllMyBooks(@PathVariable("id")Integer userID) {
        try {
            return myBooksService.findAllMyBooks(userID);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore caricamento MyBooks", e);
        }
    }



    @PreAuthorize("hasRole('ADMIN') OR (#userID == authentication.principal.usersInfo.id)")
    @PostMapping("/utenti/{id}/myBooks/{bookID}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String addBookToShelf(@PathVariable("id")Integer userID, @PathVariable("bookID")Integer bookID, @RequestBody() String shelf) {
        try {
            return "{ \"shelf\":\"" + myBooksService.addToShelf(userID, bookID, BookShelf.valueOf(shelf))+ "\"}";
        }
        catch(ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore aggiunta a MyBooks", e);
        }
    }
}
