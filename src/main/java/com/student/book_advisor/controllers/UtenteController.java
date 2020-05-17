package com.student.book_advisor.controllers;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.dto.*;
import com.student.book_advisor.dto.auxiliaryDTOs.LoggedUserDTO;
import com.student.book_advisor.dto.auxiliaryDTOs.MyBooksReadDTO;
import com.student.book_advisor.dto.formDTOS.UtenteFormDTO;
import com.student.book_advisor.dto.formDTOS.UtenteUpdateFormDTO;
import com.student.book_advisor.entities.UsersInfo;
import com.student.book_advisor.enums.BookShelf;
import com.student.book_advisor.security.AuthUserPrincipal;
import com.student.book_advisor.services.BookRankingService;
import com.student.book_advisor.services.MyBooksService;
import com.student.book_advisor.services.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;
    @Autowired
    private MyBooksService myBooksService;
    @Autowired
    private BookRankingService bookRankingService;


    @GetMapping("/utenti/loggedUserInfo")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public LoggedUserDTO getLoggedUserInfo() {
        AuthUserPrincipal authUserPrincipal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new LoggedUserDTO(authUserPrincipal.getId(), authUserPrincipal.getAuthoritiesToString().contains("ROLE_ADMIN"));
    }
    //Rimuovibile
    @GetMapping("/utenti/id")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Integer getUserID(HttpServletRequest request, HttpServletResponse response) {
        try {
            Integer id = ((AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            return id;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/utenti/isUsernameUnique")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public boolean verifyUsernameUniqueness(@RequestBody() String username) {
        return utenteService.isUsernameUnique(username);
    }

    @PostMapping("/utenti/isEmailUnique")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public boolean verifyEmailUniqueness(@RequestBody() String email) {
        return utenteService.isEmailUnique(email);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/utenti")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<UtenteCardDTO> getAllUsers(HttpServletRequest request, HttpServletResponse response) {
        try {
            return (List<UtenteCardDTO>) utenteService.findAllUsers();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/utenti/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public UsersInfoDTO getUser(@PathVariable("id") @Min(1) @Max(1) Integer id) {
        return (UsersInfoDTO) utenteService.findById(id);
    }

    @PostMapping("/utenti")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> newUser(@Valid @RequestBody UtenteFormDTO userForm, BindingResult result) {
        Map<String, Set<String>> errors = new HashMap<>();
        try {
            if(!this.utenteService.isUsernameUnique(userForm.getUsername())) {
                errors.computeIfAbsent("username", key -> new HashSet<>()).add("usernameTaken");
            }
            if(!this.utenteService.isEmailUnique(userForm.getEmail())) {
                errors.computeIfAbsent("username", key -> new HashSet<>()).add("emailTaken");
            }
            for(FieldError fieldError: result.getFieldErrors()) {
                String code = fieldError.getCode();
                String field = fieldError.getField();
                if(code.equals("NotBlank")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("required");
                }
                else if(code.equals("Size") && field.equals("nome")) {
                    if(userForm.getNome().length() < 2) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                    }
                    else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                }
                else if(code.equals("Size") && field.equals("cognome")) {
                    if(userForm.getCognome().length() < 2) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                    }
                    else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                }
                else if(code.equals("Size") && field.equals("username")) {
                    if(userForm.getUsername().length() < 5) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                    }
                    else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                }
                else if(code.equals("Size") && field.equals("password")) {
                    if(userForm.getPassword().length() < 8) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                    }
                    else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                }
                else if(code.equals("Size") && field.equals("descrizione")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                }
                else if(code.equals("Email") && field.equals("email")) {
                    errors.computeIfAbsent(field, key -> new HashSet<>()).add("email");
                }

            }
            if(errors.isEmpty()) {
                utenteService.newUser(userForm);
            }
            return errors;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PreAuthorize("hasRole('ADMIN') OR (#id == authentication.principal.usersInfo.id)")
    @PutMapping("/utenti/{id}/foto_profilo")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String updateUsersProfilePhoto(@RequestParam("fotoProfilo") MultipartFile profilePhoto, @PathVariable("id")Integer userId, HttpServletRequest request, HttpServletResponse response) {
        try {
            return utenteService.updateUsersProfilePhoto(profilePhoto, utenteService.getUser(userId));
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') OR (#id == authentication.principal.usersInfo.id)")
    @PutMapping("/utenti/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> updateUser(@Valid @RequestBody UtenteUpdateFormDTO userForm, BindingResult result, @PathVariable("id") Integer userId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Set<String>> errors = new HashMap<>();
        try {
            UsersInfo updatedUser = utenteService.getUser(userId);
            if(updatedUser != null) {
                if (!userForm.getEmail().equals(updatedUser.getEmail()) && !this.utenteService.isEmailUnique(userForm.getEmail())) {
                    errors.computeIfAbsent("username", key -> new HashSet<>()).add("emailTaken");
                }
                for (FieldError fieldError : result.getFieldErrors()) {
                    String code = fieldError.getCode();
                    String field = fieldError.getField();
                    if (code.equals("NotBlank")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("required");
                    } else if (code.equals("Size") && field.equals("nome")) {
                        if (userForm.getNome().length() < 2) {
                            errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                        } else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                    } else if (code.equals("Size") && field.equals("cognome")) {
                        if (userForm.getCognome().length() < 2) {
                            errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                        } else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                    } else if (code.equals("Size") && field.equals("password")) {
                        if (userForm.getPassword().length() < 8) {
                            errors.computeIfAbsent(field, key -> new HashSet<>()).add("minlength");
                        } else errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                    } else if (code.equals("Size") && field.equals("descrizione")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                    } else if (code.equals("Email") && field.equals("email")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("email");
                    }

                }
                if (errors.isEmpty()) {
                    utenteService.updateUser(updatedUser, userForm);
                }
                return errors;
            }
            else throw new ApplicationException("User doesn't exist!");

        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PreAuthorize("hasRole('ADMIN') OR (#id == authentication.principal.usersInfo.id)")
    @DeleteMapping("/utenti/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteUser(@PathVariable("id") @Min(1) @Max(1) Integer id, HttpServletRequest request, HttpServletResponse response) {
        try {
            utenteService.deleteUser(id);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PreAuthorize("hasRole('ADMIN') OR (#id == authentication.principal.usersInfo.id)")
    @DeleteMapping("/utenti/{id}/myBooks/{myBookID}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteBookFromMyBooks(@PathVariable("id") @Min(1) @Max(1) Integer userID, @PathVariable("myBookID") @Min(1) @Max(1) Integer myBookID, HttpServletRequest request, HttpServletResponse response) {
        try {
            myBooksService.deleteFromShelf(userID, myBookID);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') OR (#id == authentication.principal.usersInfo.id)")
    @PutMapping("/utenti/{id}/myBooks/{bookID}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String updateBookFromMyBooks(@PathVariable("id") @Min(1) @Max(1) Integer userID, @PathVariable("bookID") @Min(1) @Max(1) Integer bookID, @RequestParam("shelf") BookShelf shelf) {
        try {
            return myBooksService.updateShelf(userID, bookID, shelf);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') OR (#id == authentication.principal.usersInfo.id)")
    @GetMapping("/utenti/{id}/myBooks/booksReadNotInRank")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<MyBooksReadDTO> getAllMyBooksReadNotInRank(@PathVariable("id")Integer userID) {
        try {
            return myBooksService.findAllMyBooksRead(userID);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/utenti/{id}/myBooks")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<MyBooksDTO> getAllMyBooks(@PathVariable("id")Integer userID) {
        try {
            return myBooksService.findAllMyBooks(userID);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }



    @PreAuthorize("hasRole('ADMIN') OR (#id == authentication.principal.usersInfo.id)")
    @PostMapping("/utenti/{id}/myBooks/{bookID}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String addBookToShelf(@PathVariable("id")Integer userID, @PathVariable("bookID")Integer bookID, @RequestBody BookShelf shelf) {
        try {
            return myBooksService.addToShelf(userID, bookID, shelf);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/utenti/{id}/bookRank")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<BookRankingDTO> getUsersBookRanking(@PathVariable("id")Integer userID) {
        try {
            return bookRankingService.findUsersBookRank(userID);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') OR (#id == authentication.prinicpal.usersInfo.id)")
    @PostMapping("/utenti/{id}/bookRank")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<BookRankingDTO> addBookRank(@PathVariable("id")Integer userID, @RequestParam(name = "myBookID") Integer myBookID, @RequestParam(name = "rank")Integer rank) {
        try {
            return bookRankingService.addBookToBookRank(userID, myBookID, rank);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') OR (#id == authentication.principal.usersInfo.id)")
    @DeleteMapping("/utenti/{id}/bookRank/{rankID}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<BookRankingDTO> removeBookRank(@PathVariable("id")Integer userID, @PathVariable("rankID")Integer rankID) {
        try {
            return bookRankingService.removeBookFromBookRank(userID, rankID);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }


}
