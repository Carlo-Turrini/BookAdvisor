package com.student.book_advisor.controllers;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.*;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.LoggedUserDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.UtenteFormDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.UtenteUpdateFormDTO;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.security.AuthUserPrincipal;
import com.student.book_advisor.security.JwtTokenProvider;
import com.student.book_advisor.services.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/api")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PreAuthorize("hasRole('ADMIN') OR hasRole('USER')")
    @GetMapping("/utenti/loggedUserInfo")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public LoggedUserDTO getLoggedUserInfo() {
        AuthUserPrincipal authUserPrincipal = (AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new LoggedUserDTO(authUserPrincipal.getId(), authUserPrincipal.getAuthoritiesToString().contains("ROLE_ADMIN"));
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
            return utenteService.findAllUsers();
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore caricamento utenti", e);
        }

    }

    @GetMapping("/utenti/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public UsersInfoDTO getUser(@PathVariable("id") @Min(1) @Max(1) Integer id) {
        return utenteService.findById(id);
    }

    @PostMapping("/utenti")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> newUser(@Valid @RequestBody() UtenteFormDTO userForm, BindingResult result) {
        Map<String, Set<String>> errors = new HashMap<>();
        try {
            if(!this.utenteService.isUsernameUnique(userForm.getUsername())) {
                errors.computeIfAbsent("username", key -> new HashSet<>()).add("usernameTaken");
            }
            if(!this.utenteService.isEmailUnique(userForm.getEmail())) {
                errors.computeIfAbsent("email", key -> new HashSet<>()).add("emailTaken");
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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore aggiunta utente", e);
        }

    }

    @PreAuthorize("hasRole('ADMIN') OR (#userId == authentication.principal.usersInfo.id)")
    @PostMapping("/utenti/{id}/foto_profilo")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String updateUsersProfilePhoto(@RequestPart("fotoProfilo") MultipartFile profilePhoto, @PathVariable("id")Integer userId, HttpServletRequest request, HttpServletResponse response) {
        try {
            return utenteService.updateUsersProfilePhoto(profilePhoto, userId);
        }
        catch(ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore aggiornamento foto utente", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') OR (#userId == authentication.principal.usersInfo.id)")
    @PutMapping("/utenti/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> updateUser(@Valid @RequestBody UtenteUpdateFormDTO userForm, BindingResult result, @PathVariable("id") Integer userId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Set<String>> errors = new HashMap<>();
        try {
            UsersInfo updatedUser = utenteService.getUser(userId);
            if(updatedUser != null) {
                if (!userForm.getEmail().equals(updatedUser.getEmail()) && !this.utenteService.isEmailUnique(userForm.getEmail())) {
                    errors.computeIfAbsent("email", key -> new HashSet<>()).add("emailTaken");
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
                    }  else if (code.equals("Size") && field.equals("descrizione")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("maxlength");
                    } else if (code.equals("Email") && field.equals("email")) {
                        errors.computeIfAbsent(field, key -> new HashSet<>()).add("email");
                    }
                }
                if(userForm.getPassword()!="" && userForm.getPassword()!=null) {
                    if(userForm.getPassword().length()<8) {
                        errors.computeIfAbsent("password", key -> new HashSet<>()).add("minlength");
                    }
                    else if(userForm.getPassword().length()>50) {
                        errors.computeIfAbsent("password", key -> new HashSet<>()).add("maxlength");
                    }
                }
                if (errors.isEmpty()) {
                    utenteService.updateUser(updatedUser, userForm);
                }
                return errors;
            }
            else throw new ApplicationException("User doesn't exist!");

        }
        catch(ApplicationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore aggiornamento utente", e);
        }

    }

    @PreAuthorize("hasRole('ADMIN') OR (#id == authentication.principal.usersInfo.id)")
    @DeleteMapping("/utenti/{id}")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteUser(@PathVariable("id") @Min(1) @Max(1) Integer id, HttpServletRequest request, HttpServletResponse response) {
        try {
            if(id == ((AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId()) {
                SecurityContextHolder.getContext().setAuthentication(null);
                jwtTokenProvider.invalidateJwtToken(request, response);
                response.setStatus(HttpServletResponse.SC_OK);
            }
            utenteService.deleteUser(id);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore cancellazione utente", e);
        }
    }
}
