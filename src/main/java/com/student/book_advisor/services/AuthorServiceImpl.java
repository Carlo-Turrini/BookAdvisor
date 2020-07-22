package com.student.book_advisor.services;

import com.student.book_advisor.services.storage.Constants;
import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.AuthorCardDTO;
import com.student.book_advisor.data_persistency.model.dto.AuthorDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.AuthorFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Author;
import com.student.book_advisor.data_persistency.repositories.AuthorRepository;
import com.student.book_advisor.services.storage.FileUploadDir;
import com.student.book_advisor.services.storage.StorageService;
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
        newAuthor.setBiography(author.getBiography());
        authorRepository.save(newAuthor);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isAuthorsFullnameUnique(String authorsFullname) {
        return authorRepository.countAllAuthorsWithName(authorsFullname) == 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Author getAuthor(Integer id) {
        return authorRepository.getOne(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AuthorDTO getAuthorsDTO(Integer id) {
        AuthorDTO author = authorRepository.getAuthorsDTOById(id);
        if(author != null) {
            String authorsPhotoPath = authorRepository.getAuthorsPhotoPath(id);
            if (authorsPhotoPath.equals(Constants.DEF_PROFILE_PIC)) {
                author.setAuthorsPhoto(authorsPhotoPath);
            } else {
                author.setAuthorsPhoto(storageService.serve(authorsPhotoPath, FileUploadDir.authorImage));
            }
        }
        return author;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<AuthorCardDTO> getAllAuthors() {
        List<AuthorCardDTO> authorCardDTOList = authorRepository.findAllToDTO();
        for(AuthorCardDTO author : authorCardDTOList) {
            String authorsPhotoPath = authorRepository.getAuthorsPhotoPath(author.getId());
            if(authorsPhotoPath.equals(Constants.DEF_PROFILE_PIC)) {
                author.setAuthorsPhoto(authorsPhotoPath);
            }
            else {
                author.setAuthorsPhoto(storageService.serve(authorsPhotoPath, FileUploadDir.authorImage));
            }
        }
        return authorCardDTOList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAuthor(Integer id) {
        String photoPath = authorRepository.getAuthorsPhotoPath(id);
        if(photoPath != Constants.DEF_PROFILE_PIC) {
            storageService.delete(photoPath, FileUploadDir.authorImage);
        }
        authorRepository.deleteById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAuthor(Author author, AuthorFormDTO updateAuthorInfo) {
        author.setBiography(updateAuthorInfo.getBiography());
        author.setDeathYear(updateAuthorInfo.getDeathYear());
        author.setBirthYear(updateAuthorInfo.getBirthYear());
        author.setAuthorsFullname(updateAuthorInfo.getAuthorsFullname());
        authorRepository.save(author);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<AuthorOfBook> getAuthorsOfBook(Integer bookID) {
        return authorRepository.findAuthorsOfBook(bookID);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String updateAuthorsPhoto(MultipartFile authorsPhoto, Integer authorsID) {
        try {
            Author author = authorRepository.findById(authorsID).orElse(null);
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

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<AuthorOfBook> getAllAuthorsOfBook() {
        return this.authorRepository.findAllAuthorsOfBook();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AuthorOfBook getAuthorOfBookByFullname(String authorsFullname) {
        return this.authorRepository.getAuthorOfBookByFullname(authorsFullname);
    }
}
