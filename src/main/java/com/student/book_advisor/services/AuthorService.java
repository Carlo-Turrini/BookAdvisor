package com.student.book_advisor.services;

import com.student.book_advisor.dto.AuthorCardDTO;
import com.student.book_advisor.dto.AuthorDTO;
import com.student.book_advisor.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.dto.formDTOS.AuthorFormDTO;
import com.student.book_advisor.entities.Author;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AuthorService {

    public void addAuthor(AuthorFormDTO author);
    public boolean isAuthorsFullnameUnique(String authorsFullname);
    public Author getAuthor(Long id);
    public List<AuthorCardDTO> getAllAuthors();
    public void deleteAuthor(Long id);
    public void updateAuthor(Author author, AuthorFormDTO updateAuthorInfo);
    public AuthorDTO getAuthorsDTO(Long id);
    public List<AuthorOfBook> getAuthorsOfBook(Long bookID);
    public String updateAuthorsPhoto(MultipartFile authorsPhoto, Long authorsID);
    public List<AuthorOfBook> getAllAuthorsOfBook();
    public AuthorOfBook getAuthorOfBookByFullname(String authorsFullname);
}
