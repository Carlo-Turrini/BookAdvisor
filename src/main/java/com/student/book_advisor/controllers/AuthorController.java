package com.student.book_advisor.controllers;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.AuthorCardDTO;
import com.student.book_advisor.data_persistency.model.dto.AuthorDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.AuthorFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Author;
import com.student.book_advisor.services.AuthorService;
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

import javax.validation.Valid;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthorController {
    @Autowired
    private AuthorService authorService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/authors/isFullnameUnique")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Boolean isAuthorsFullnameUnique(@RequestBody()String authorsFullname) {
        return authorService.isAuthorsFullnameUnique(authorsFullname);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/authors")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> addAuthor(@Valid @RequestBody AuthorFormDTO authorFormDTO, BindingResult result) {
        Map<String, Set<String>> errors = new HashMap<String, Set<String>>();
        try {
            if(!authorService.isAuthorsFullnameUnique(authorFormDTO.getAuthorsFullname())) {
                errors.computeIfAbsent("authorsFullname", key -> new HashSet<String>()).add("fullnameTaken");
            }
            for(FieldError fieldError : result.getFieldErrors()) {
                String code = fieldError.getCode();
                String field = fieldError.getField();
                if(code.equals("NotBlank") || code.equals("NotNull")) {
                    errors.computeIfAbsent(field, key -> new HashSet<String>()).add("required");
                }
                else if(code.equals("Min")) {
                    errors.computeIfAbsent(field, key-> new HashSet<String>()).add("min");
                }
                else if(code.equals("Max")) {
                    errors.computeIfAbsent(field, key-> new HashSet<String>()).add("max");
                }
                else if(code.equals("Size")) {
                    String errorCode = null;
                    if(field.equals("authorsFullname")) {
                        if(authorFormDTO.getAuthorsFullname().length() < 3) {
                            errorCode = "minLength";
                        }
                        else errorCode = "maxLength";
                    }
                    else if(field.equals("biography")) {
                        if(authorFormDTO.getBiography().length() < 1) {
                            errorCode = "minLength";
                        }
                        else errorCode = "maxLength";
                    }
                    errors.computeIfAbsent(field, key -> new HashSet<String>()).add(errorCode);
                }
            }
            if(errors.isEmpty()) {
                authorService.addAuthor(authorFormDTO);
            }
            return errors;
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore aggiunta autore", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/authors/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> updateAuthor(@Valid @RequestBody AuthorFormDTO authorFormDTO, BindingResult result, @PathVariable("id")Integer id) {
        Map<String, Set<String>> errors = new HashMap<String, Set<String>>();
        try {
            Author author = authorService.getAuthor(id);
            if(author != null) {
                if (!author.getAuthorsFullname().equals(authorFormDTO.getAuthorsFullname()) && !authorService.isAuthorsFullnameUnique(authorFormDTO.getAuthorsFullname())) {
                    errors.computeIfAbsent("authorsFullname", key -> new HashSet<String>()).add("fullnameTaken");
                }
                for (FieldError fieldError : result.getFieldErrors()) {
                    String code = fieldError.getCode();
                    String field = fieldError.getField();
                    if (code.equals("NotBlank") || code.equals("NotNull")) {
                        errors.computeIfAbsent(field, key -> new HashSet<String>()).add("required");
                    } else if (code.equals("Min")) {
                        errors.computeIfAbsent(field, key -> new HashSet<String>()).add("min");
                    } else if (code.equals("Max")) {
                        errors.computeIfAbsent(field, key -> new HashSet<String>()).add("max");
                    } else if (code.equals("Size")) {
                        String errorCode = null;
                        if (field.equals("authorsFullname")) {
                            if (authorFormDTO.getAuthorsFullname().length() < 3) {
                                errorCode = "minLength";
                            } else errorCode = "maxLength";
                        } else if (field.equals("biography")) {
                            if (authorFormDTO.getBiography().length() < 1) {
                                errorCode = "minLength";
                            } else errorCode = "maxLength";
                        }
                        errors.computeIfAbsent(field, key -> new HashSet<String>()).add(errorCode);
                    }
                }
                if (errors.isEmpty()) {
                    authorService.updateAuthor(author, authorFormDTO);
                }
            }
            else throw new ApplicationException("This author doesn't exist");
            return errors;
        }
        catch(ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore aggiornamento autore", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/authors/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAuthor(@PathVariable("id")Integer id){
        try {
            authorService.deleteAuthor(id);

        }
        catch (ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore cancellazione autore", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/authors/authorsForBookForm")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<AuthorOfBook> getAuthorsForBookForm() {
        return this.authorService.getAllAuthorsOfBook();
    }

    @GetMapping("/authors/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public AuthorDTO getAuthor(@PathVariable("id")Integer id) {
        return authorService.getAuthorsDTO(id);
    }

    @GetMapping("/authors")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<AuthorCardDTO> getAllAuthors() {
        return authorService.getAllAuthors();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/authors/{id}/authors_photo")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String updateAuthorsPhoto(@RequestPart("authorsPhoto") MultipartFile authorsPhoto, @PathVariable("id")Integer authorID) {
        try {
            return authorService.updateAuthorsPhoto(authorsPhoto, authorID);
        }
        catch(ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore aggiornamento foto autore", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/authors/byName/{fullname}")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public AuthorOfBook getAuthorOfBookFromFullname(@PathVariable("fullname")String authorsFullname) {
        return this.authorService.getAuthorOfBookByFullname(authorsFullname);
    }

}
