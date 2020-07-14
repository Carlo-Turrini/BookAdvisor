package com.student.book_advisor.services;

import com.student.book_advisor.data_persistency.model.dto.AuthorCardDTO;
import com.student.book_advisor.data_persistency.model.dto.AuthorDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.AuthorFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Author;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AuthorService {

    public void addAuthor(AuthorFormDTO author);
    public boolean isAuthorsFullnameUnique(String authorsFullname);
    public Author getAuthor(Integer id);
    public List<AuthorCardDTO> getAllAuthors();
    public void deleteAuthor(Integer id);
    public void updateAuthor(Author author, AuthorFormDTO updateAuthorInfo);
    public AuthorDTO getAuthorsDTO(Integer id);
    public List<AuthorOfBook> getAuthorsOfBook(Integer bookID);
    public String updateAuthorsPhoto(MultipartFile authorsPhoto, Integer authorsID);
    public List<AuthorOfBook> getAllAuthorsOfBook();
    public AuthorOfBook getAuthorOfBookByFullname(String authorsFullname);
}
