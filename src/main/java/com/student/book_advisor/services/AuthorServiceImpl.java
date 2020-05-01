package com.student.book_advisor.services;

import com.student.book_advisor.constants.Constants;
import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.dto.AuthorCardDTO;
import com.student.book_advisor.dto.AuthorDTO;
import com.student.book_advisor.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.dto.formDTOS.AuthorFormDTO;
import com.student.book_advisor.entities.Author;
import com.student.book_advisor.entityRepositories.AuthorRepository;
import com.student.book_advisor.enums.FileUploadDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private StorageService storageService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addAuthor(AuthorFormDTO author) {
        Author newAuthor = new Author();
        newAuthor.setAuthorsFullname(author.getAuthorsFullname());
        newAuthor.setBirthYear(author.getBirthYear());
        newAuthor.setDeathYear(author.getDeathYear());
        newAuthor.setBibliography(author.getBibliography());
        authorRepository.save(newAuthor);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isAuthorsFullnameUnique(String authorsFullname) {
        return authorRepository.countAllAuthorsWithName(authorsFullname) > 0 ? false : true;
    }

    @Override
    public Author getAuthor(Long id) {
        return authorRepository.getOne(id);
    }

    @Override
    public AuthorDTO getAuthorsDTO(Long id) {
        return authorRepository.getAuthorsDTOById(id);
    }

    @Override
    public List<AuthorCardDTO> getAllAuthors() {
        return authorRepository.findAllToDTO();
    }

    @Override
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    @Override
    public void updateAuthor(Author author, AuthorFormDTO updateAuthorInfo) {
        author.setBibliography(updateAuthorInfo.getBibliography());
        author.setDeathYear(updateAuthorInfo.getDeathYear());
        author.setBirthYear(updateAuthorInfo.getBirthYear());
        author.setAuthorsFullname(updateAuthorInfo.getAuthorsFullname());
        authorRepository.save(author);
    }

    @Override
    public List<AuthorOfBook> getAuthorsOfBook(Long bookID) {
        return authorRepository.findAuthorsOfBook(bookID);
    }

    @Override
    public String updateAuthorsPhoto(MultipartFile authorsPhoto, Long authorsID) {
        try {
            Author author = authorRepository.getOne(authorsID);
            if (author != null) {
                String photoPath = null;
                if(!author.getAuthorsPhotoPath().equals(Constants.DEF_PROFILE_PIC)) {
                    photoPath = author.getAuthorsPhotoPath();
                }
                String filePath = storageService.store(authorsPhoto, FileUploadDir.authorImage, photoPath);
                author.setAuthorsPhotoPath(filePath);
                authorRepository.save(author);
                String src = "{ \"img\":\""+storageService.serve(filePath, FileUploadDir.authorImage) + "\"}";
                return src;
            } else throw new ApplicationException("This author doesn't exist!");
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}