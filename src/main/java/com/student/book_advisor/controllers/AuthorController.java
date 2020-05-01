package com.student.book_advisor.controllers;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.dto.AuthorCardDTO;
import com.student.book_advisor.dto.AuthorDTO;
import com.student.book_advisor.dto.formDTOS.AuthorFormDTO;
import com.student.book_advisor.entities.Author;
import com.student.book_advisor.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthorController {
    @Autowired
    private AuthorService authorService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/authors/isFullnameUnique")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Boolean isAuthorsFullnameUnique(@RequestParam("fullname")String authorsFullname) {
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
                    else if(field.equals("bibliography")) {
                        if(authorFormDTO.getBibliography().length() < 1) {
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
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/authors/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> updateAuthor(@Valid @RequestBody AuthorFormDTO authorFormDTO, BindingResult result, @PathVariable("id")Long id) {
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
                        } else if (field.equals("bibliography")) {
                            if (authorFormDTO.getBibliography().length() < 1) {
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
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/authors/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAuthor(@RequestParam("id")Long id){
        authorService.deleteAuthor(id);
    }

    @GetMapping("/authors/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public AuthorDTO getAuthor(@RequestParam("id")Long id) {
        return authorService.getAuthorsDTO(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/authors")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<AuthorCardDTO> getAllAuthors() {
        return authorService.getAllAuthors();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/authors/{id}/authors_photo")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String updateAuthorsPhoto(@RequestParam("authorsPhoto")MultipartFile authorsPhoto, @PathVariable("id")Long authorID) {
        try {
            return authorService.updateAuthorsPhoto(authorsPhoto, authorID);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}