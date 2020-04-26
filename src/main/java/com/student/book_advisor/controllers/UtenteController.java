package com.student.book_advisor.controllers;


import com.student.book_advisor.constants.Constants;
import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.dto.LoginDTO;
import com.student.book_advisor.dto.UtenteCardDTO;
import com.student.book_advisor.dto.UtenteDTO;
import com.student.book_advisor.dto.formDTOS.UtenteFormDTO;
import com.student.book_advisor.entities.Utente;
import com.student.book_advisor.enums.Credenziali;
import com.student.book_advisor.services.UtenteService;
import com.student.book_advisor.session.LoggedUserDAO;
import com.student.book_advisor.session.SessionDAOFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:4200")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Boolean login(@RequestBody()LoginDTO loginInfo, HttpServletRequest request, HttpServletResponse response) {
        SessionDAOFactory session;
        String authToken;
        try {

            System.out.println(loginInfo.getUsername() + " " + loginInfo.getPassword());
            session = SessionDAOFactory.getSesssionDAOFactory(Constants.SESSION_IMPL);
            session.initSession(request, response);
            LoggedUserDAO loggedUserDAO = session.getLoggedUserDAO();
            Utente userToLogin = utenteService.findUserToLogin(loginInfo.getUsername(), loginInfo.getPassword());
            if (userToLogin != null) {
                authToken = userToLogin.getAuthToken();
                loggedUserDAO.create(authToken);
                System.out.println(userToLogin.getEmail());
                return true;
            }
            else {
                return false;
            }
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/utenti/id")
    @CrossOrigin(origins = "*",allowCredentials = "true")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Long getUserIdByAuthToken(HttpServletRequest request, HttpServletResponse response) {
        SessionDAOFactory session;
        String authToken;
        try {
            session = SessionDAOFactory.getSesssionDAOFactory(Constants.SESSION_IMPL);
            session.initSession(request, response);
            LoggedUserDAO loggedUserDAO = session.getLoggedUserDAO();
            authToken = loggedUserDAO.find();
            if (authToken != null) {
                Long id = utenteService.findIdByAuthToken(authToken);
                System.out.println("Fancu " + id);
                return id;
            }
            else throw new ApplicationException("Utente non loggato!");
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/utenti/credenziali")
    @CrossOrigin(origins = "http://localhost:4200")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Credenziali getUserCredentialsByAuthToken(HttpServletRequest request, HttpServletResponse response) {
        SessionDAOFactory session;
        String authToken;
        try {
            session = SessionDAOFactory.getSesssionDAOFactory(Constants.SESSION_IMPL);
            session.initSession(request, response);
            LoggedUserDAO loggedUserDAO = session.getLoggedUserDAO();
            authToken = loggedUserDAO.find();
            if (authToken != null) {
                return utenteService.findUsersCredentials(authToken);
            }
            else throw new ApplicationException("Utente non loggato!");
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/logout")
    @CrossOrigin(origins = "http://localhost:4200")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SessionDAOFactory session;
        try {
            session = SessionDAOFactory.getSesssionDAOFactory(Constants.SESSION_IMPL);
            session.initSession(request, response);
            LoggedUserDAO loggedUserDAO = session.getLoggedUserDAO();
            loggedUserDAO.destroy();
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/utenti/isUsernameUnique")
    @CrossOrigin(origins = "http://localhost:4200")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public boolean verifyUsernameUniqueness(@RequestBody() String username) {
        return utenteService.isUsernameUnique(username);
    }

    @PostMapping("/utenti/isEmailUnique")
    @CrossOrigin(origins = "http://localhost:4200")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public boolean verifyEmailUniqueness(@RequestBody() String email) {
        return utenteService.isEmailUnique(email);
    }

    @GetMapping("/utenti")
    @CrossOrigin(origins = "http://localhost:4200")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<UtenteCardDTO> getAllUsers(HttpServletRequest request, HttpServletResponse response) {
        SessionDAOFactory session;
        String authToken;
        try {
            session = SessionDAOFactory.getSesssionDAOFactory(Constants.SESSION_IMPL);
            session.initSession(request, response);
            LoggedUserDAO loggedUserDAO = session.getLoggedUserDAO();
            authToken = loggedUserDAO.find();
            if(authToken != null) {
                if(utenteService.findUsersCredentials(authToken).equals(Credenziali.Admin)) {
                    return (List<UtenteCardDTO>) utenteService.findAllUsers();
                }
                else throw new ApplicationException("Accesso negato");
            }
            else throw new ApplicationException("Utente non loggato");
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/utenti/{id}")
    @CrossOrigin(origins = "http://localhost:4200")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public UtenteDTO getUser(@PathVariable("id") @Min(1) @Max(1) Long id) {
        return (UtenteDTO) utenteService.findById(id);
    }

    @PostMapping("/utenti")
    @CrossOrigin(origins = "http://localhost:4200")
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
                Utente newUser = new Utente();
                newUser.setNome(userForm.getNome());
                newUser.setCognome(userForm.getCognome());
                newUser.setUsername(userForm.getUsername());
                newUser.setPassword(userForm.getPassword());
                newUser.setEmail(userForm.getEmail());
                newUser.setDescrizione(userForm.getDescrizione());
                utenteService.newUser(newUser);
            }
            return errors;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PutMapping("/utenti/{id}/foto_profilo")
    @CrossOrigin(origins = "http://localhost:4200")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String updateUsersProfilePhoto(@RequestParam("fotoProfilo") MultipartFile profilePhoto, @PathVariable("id")Long userId, HttpServletRequest request, HttpServletResponse response) {
        SessionDAOFactory session;
        String authToken;
        try {
            session = SessionDAOFactory.getSesssionDAOFactory(Constants.SESSION_IMPL);
            session.initSession(request, response);
            LoggedUserDAO loggedUserDAO = session.getLoggedUserDAO();
            authToken = loggedUserDAO.find();
            if(authToken != null) {
                Utente loggedUser = utenteService.findUserByAuthToken(authToken);
                if(loggedUser.getId() == userId || loggedUser.getCredenziali().equals(Credenziali.Admin)) {
                    return utenteService.updateUsersProfilePhoto(profilePhoto, loggedUser);
                }
                else throw new ApplicationException("Accesso negato!");
            }
            else throw new ApplicationException("Utente non loggato!");
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/utenti/{id}")
    @CrossOrigin(origins = "http://localhost:4200")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, Set<String>> updateUser(@Valid @RequestBody UtenteFormDTO userForm, BindingResult result, @PathVariable("id") Long userId, HttpServletRequest request, HttpServletResponse response) {
        SessionDAOFactory session;
        String authToken;
        Map<String, Set<String>> errors = new HashMap<>();
        try {
            session = SessionDAOFactory.getSesssionDAOFactory(Constants.SESSION_IMPL);
            session.initSession(request, response);
            LoggedUserDAO loggedUserDAO = session.getLoggedUserDAO();
            authToken = loggedUserDAO.find();
            if(authToken != null) {
                Long loggedUserId = utenteService.findIdByAuthToken(authToken);
                if(userId == loggedUserId || utenteService.findUsersCredentials(authToken).equals(Credenziali.Admin)) {
                    Utente updatedUser = utenteService.getUser(userId);
                    if(!userForm.getUsername().equals(updatedUser.getUsername()) && !this.utenteService.isUsernameUnique(userForm.getUsername())) {
                        errors.computeIfAbsent("username", key -> new HashSet<>()).add("usernameTaken");
                    }
                    if(!userForm.getEmail().equals(updatedUser.getEmail()) && !this.utenteService.isEmailUnique(userForm.getEmail())) {
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
                        updatedUser.setNome(userForm.getNome());
                        updatedUser.setCognome(userForm.getCognome());
                        updatedUser.setUsername(userForm.getUsername());
                        updatedUser.setPassword(userForm.getPassword());
                        updatedUser.setEmail(userForm.getEmail());
                        updatedUser.setDescrizione(userForm.getDescrizione());
                        updatedUser.setCredenziali(userForm.getCredenziali());
                        utenteService.updateUser(updatedUser);
                    }
                    return errors;
                }
                else throw new ApplicationException("Accesso negato");
            }
            else throw new ApplicationException("Utente non loggato");

        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    @DeleteMapping("/utenti/{id}")
    @CrossOrigin(origins = "http://localhost:4200")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteUser(@PathVariable("id") @Min(1) @Max(1) Long id, HttpServletRequest request, HttpServletResponse response) {
        SessionDAOFactory session;
        String authToken;
        try {
            session = SessionDAOFactory.getSesssionDAOFactory(Constants.SESSION_IMPL);
            session.initSession(request, response);
            LoggedUserDAO loggedUserDAO = session.getLoggedUserDAO();
            authToken = loggedUserDAO.find();
            if(authToken != null) {
                Long loggedUserId = utenteService.findIdByAuthToken(authToken);
                if(loggedUserId == id || utenteService.findUsersCredentials(authToken).equals(Credenziali.Admin)) {
                    utenteService.deleteUser(id);
                }
                else throw new ApplicationException("Accesso negato");
             }
            else throw new ApplicationException("Utente non loggato");
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

}
