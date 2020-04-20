package com.student.book_advisor.services;

import com.student.book_advisor.dto.UtenteCardDTO;
import com.student.book_advisor.dto.UtenteDTO;
import com.student.book_advisor.entities.Utente;
import com.student.book_advisor.enums.Credenziali;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UtenteService {

    public List<UtenteCardDTO> findAllUsers();

    public UtenteDTO findById(Long id);

    public Utente getUser(Long userId);

    public Utente newUser(Utente newUser);

    public Utente updateUser(Utente updatedUser);

    public void deleteUser(Long id);

    public Utente findUserToLogin(String username, String password);

    public Credenziali findUsersCredentials(String authToken);

    public Long findIdByAuthToken(String authToken);

    public boolean isUsernameUnique(String username);

    public boolean isEmailUnique(String email);

    public Utente findUserByAuthToken(String authToken);

    public String updateUsersProfilePhoto(MultipartFile profilePhoto, Utente user);
}
