package com.student.book_advisor.services;

import com.student.book_advisor.dto.*;
import com.student.book_advisor.entities.UsersInfo;
import com.student.book_advisor.entities.Utente;
import com.student.book_advisor.enums.BookShelf;
import com.student.book_advisor.enums.Credenziali;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UtenteService {

    public List<UtenteCardDTO> findAllUsers();

    public UsersInfoDTO findById(Long id);

    public UsersInfo getUser(Long userId);

    public UsersInfo newUser(UsersInfo newUser);

    public UsersInfo updateUser(UsersInfo updatedUser);

    public void deleteUser(Long id);

    //public UsersInfo findUserToLogin(String username, String password);

    public boolean isUsernameUnique(String username);

    public boolean isEmailUnique(String email);

    public String updateUsersProfilePhoto(MultipartFile profilePhoto, UsersInfo user);

    public void addToShelf(Long userID, Long bookID, BookShelf shef);

    public void deleteFromShelf(Long userID, Long myBookID);

    public void updateShelf(Long userID, Long myBookID, BookShelf shelf);

    public List<MyBooksDTO> findAllMyBooks(Long userID);

    public List<BookRankingDTO> findUsersBookRank(Long userID);

    public List<BookRankingDTO> addBookToBookRank(Long userID, Long bookID, Integer bookRank);

    public List<BookRankingDTO> removeBookFromBookRank(Long userID, Long bookRankID);
}
