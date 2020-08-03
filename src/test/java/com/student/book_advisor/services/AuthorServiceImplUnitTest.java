package com.student.book_advisor.services;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.AuthorCardDTO;
import com.student.book_advisor.data_persistency.model.dto.AuthorDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.AuthorFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Author;
import com.student.book_advisor.data_persistency.repositories.AuthorRepository;
import com.student.book_advisor.services.storage.Constants;
import com.student.book_advisor.services.storage.FileUploadDir;
import com.student.book_advisor.services.storage.StorageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
public class AuthorServiceImplUnitTest {

    @TestConfiguration
    static class AuthorServiceImplTestContextConfiguration {
        @Bean
        public AuthorService authorService() {
            return new AuthorServiceImpl();
        }
    }

    @Autowired
    private AuthorService authorService;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private StorageService storageService;

    Author author = null;
    AuthorDTO authorDTO = null;

    @Before
    public void setup() {
        author = new Author();
        author.setId(1);
        author.setDeathYear(1985);
        author.setBirthYear(1920);
        author.setAuthorsFullname("Pablo Picasso");
        author.setBiography("Autore 1");
        Mockito.when(authorRepository.countAllAuthorsWithName("Pablo")).thenReturn(1);
        Mockito.when(authorRepository.countAllAuthorsWithName("Mario")).thenReturn(0);
        Mockito.when(authorRepository.save(Mockito.any(Author.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(authorRepository.findById(1)).thenReturn(Optional.of(author));
        authorDTO = new AuthorDTO(1, "Pablo Picasso", "Autore 1", 1920, 1985);
        Mockito.when(authorRepository.getAuthorsDTOById(1)).thenReturn(authorDTO);
        Mockito.when(authorRepository.getAuthorsDTOById(2)).thenReturn(authorDTO);
        Mockito.when(authorRepository.getAuthorsDTOById(3)).thenReturn(null);
        Mockito.when(authorRepository.getAuthorsPhotoPath(1)).thenReturn(Constants.DEF_PROFILE_PIC);
        Mockito.when(authorRepository.getAuthorsPhotoPath(2)).thenReturn("/notDefaultImage.jpg");
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/notDefaultImage.jpg");
        List<AuthorCardDTO> authorCardDTOList = new ArrayList<AuthorCardDTO>();
        AuthorCardDTO authorCardDTO1 = new AuthorCardDTO(1, "Pablo Picasso", 1920, 1985);
        AuthorCardDTO authorCardDTO2 = new AuthorCardDTO(2, "Mario Rossi", 1950, null);
        authorCardDTOList.add(authorCardDTO1);
        authorCardDTOList.add(authorCardDTO2);
        Mockito.when(authorRepository.findAllToDTO()).thenReturn(authorCardDTOList);
    }

    @Test
    public void testAddAuthor() {
        AuthorFormDTO authorFormDTO = new AuthorFormDTO();
        authorFormDTO.setAuthorsFullname("Pablo Picasso");
        authorFormDTO.setBiography("Autore 1");
        authorFormDTO.setBirthYear(1920);
        authorFormDTO.setDeathYear(1985);
        authorService.addAuthor(authorFormDTO);
        Mockito.verify(authorRepository, Mockito.times(1)).save(Mockito.any(Author.class));
        Mockito.verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void testIsAuthorsFullnameUniqueReturnFalse() {
        Boolean result = authorService.isAuthorsFullnameUnique("Pablo");
        Mockito.verify(authorRepository, Mockito.times(1)).countAllAuthorsWithName(Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(authorRepository);
        assertThat(result).isFalse();
    }

    @Test
    public void testIsAuthorsFullnameUniqueReturnTrue() {
        Boolean result = authorService.isAuthorsFullnameUnique("Mario");
        Mockito.verify(authorRepository, Mockito.times(1)).countAllAuthorsWithName(Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(authorRepository);
        assertThat(result).isTrue();
    }

    @Test
    public void testGetAuthorPresent() {
        Author found = authorService.getAuthor(1);
        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(author);
        Mockito.verify(authorRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void testGetAuthorNotPresent() {
        Author found = authorService.getAuthor(2);
        assertThat(found).isNull();
        Mockito.verify(authorRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void testGetAuthorsDTOWithDefaultPhoto() {
        AuthorDTO found = authorService.getAuthorsDTO(1);
        assertThat(found).isNotNull();
        assertThat(found.getAuthorsFullname()).isEqualTo(authorDTO.getAuthorsFullname());
        assertThat(found.getAuthorsPhoto()).isEqualTo(Constants.DEF_PROFILE_PIC);
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorsDTOById(Mockito.anyInt());
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorsPhotoPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verifyNoInteractions(storageService);
    }

    @Test
    public void testGetAuthorsDTOWithoutDefaultPhoto() {
        AuthorDTO found = authorService.getAuthorsDTO(2);
        assertThat(found).isNotNull();
        assertThat(found.getAuthorsFullname()).isEqualTo(authorDTO.getAuthorsFullname());
        assertThat(found.getAuthorsPhoto()).isEqualTo("/notDefaultImage.jpg");
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorsDTOById(Mockito.anyInt());
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorsPhotoPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testGetAuthorsDTONotPresent() {
        AuthorDTO found = authorService.getAuthorsDTO(3);
        assertThat(found).isNull();
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorsDTOById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verifyNoInteractions(storageService);
    }

    @Test
    public void testGetAllAuthors() {
        List<AuthorCardDTO> authorCardDTOList = authorService.getAllAuthors();
        assertThat(authorCardDTOList).isNotEmpty();
        assertThat(authorCardDTOList.size()).isEqualTo(2);
        Mockito.verify(authorRepository, Mockito.times(1)).findAllToDTO();
        Mockito.verify(authorRepository, Mockito.times(2)).getAuthorsPhotoPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testGetAllAuthors_NoAuthorPresent() {
        Mockito.when(authorRepository.findAllToDTO()).thenReturn(new ArrayList<AuthorCardDTO>());
        List<AuthorCardDTO> authorCardDTOList = authorService.getAllAuthors();
        assertThat(authorCardDTOList).isEmpty();
        Mockito.verify(authorRepository, Mockito.times(1)).findAllToDTO();
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verifyNoInteractions(storageService);
    }

    @Test
    public void testDeleteAuthor_PhotoPathDefault() {
        Mockito.when(authorRepository.countAllBooksByAuthor(Mockito.anyInt())).thenReturn(0);
        authorService.deleteAuthor(1);
        Mockito.verify(authorRepository, Mockito.times(1)).countAllBooksByAuthor(Mockito.anyInt());
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorsPhotoPath(Mockito.anyInt());
        Mockito.verifyNoInteractions(storageService);
        Mockito.verify(authorRepository, Mockito.times(1)).deleteById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void testDeleteAuthor_PhotoPathNotDefault() {
        Mockito.when(authorRepository.countAllBooksByAuthor(Mockito.anyInt())).thenReturn(0);
        authorService.deleteAuthor(2);
        Mockito.verify(authorRepository, Mockito.times(1)).countAllBooksByAuthor(Mockito.anyInt());
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorsPhotoPath(Mockito.anyInt());
        Mockito.verify(storageService, Mockito.times(1)).delete(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
        Mockito.verify(authorRepository, Mockito.times(1)).deleteById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void testDeleteAuthor_HasBooks() {
        Mockito.when(authorRepository.countAllBooksByAuthor(Mockito.anyInt())).thenReturn(1);
        assertThatThrownBy(() -> authorService.deleteAuthor(1))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("Non cancellabile: l'autore possiede dei libri!");
        Mockito.verify(authorRepository, Mockito.times(1)).countAllBooksByAuthor(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verifyNoInteractions(storageService);
    }

    @Test
    public void testUpdateAuthor() {
        AuthorFormDTO authorFormDTO = new AuthorFormDTO();
        authorFormDTO.setAuthorsFullname("Pablo Picasso");
        authorFormDTO.setBiography("Modifica biografia autore 1");
        authorFormDTO.setBirthYear(1920);
        authorFormDTO.setDeathYear(1985);
        authorService.updateAuthor(author, authorFormDTO);
        Mockito.verify(authorRepository, Mockito.times(1)).save(Mockito.any(Author.class));
        Mockito.verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void testGetAuthorsOfBook() {
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Picasso");
        authorOfBook.setId(1);
        authorOfBookList.add(authorOfBook);
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(authorOfBookList);
        List<AuthorOfBook> found = authorService.getAuthorsOfBook(1);
        assertThat(found).isNotEmpty();
        assertThat(found).isEqualTo(authorOfBookList);
        Mockito.verify(authorRepository, Mockito.times(1)).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void testUpdateAuthorsPhoto() {
        Mockito.when(storageService.serve(Mockito.nullable(String.class), Mockito.any(FileUploadDir.class))).thenReturn("test");
        Mockito.when(storageService.store(Mockito.any(MultipartFile.class), Mockito.any(FileUploadDir.class), Mockito.nullable(String.class))).thenReturn("test");
        Author author = new Author();
        Mockito.when(authorRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(author));
        MultipartFile mf = new MockMultipartFile("test", "test".getBytes());
        String found = authorService.updateAuthorsPhoto(mf, 1);
        assertThat(found).isNotNull();
        assertThat(found).isEqualTo("{ \"img\":\"test\"}");
        Mockito.verify(authorRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verify(storageService, Mockito.times(1)).store(Mockito.any(MultipartFile.class), Mockito.any(FileUploadDir.class), Mockito.nullable(String.class));
        Mockito.verify(authorRepository, Mockito.times(1)).save(Mockito.any(Author.class));
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.nullable(String.class), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testUpdateAuthorsPhoto_authorNotPresent() {
        MultipartFile mf = new MockMultipartFile("test", "test".getBytes());
        assertThatThrownBy(() -> authorService.updateAuthorsPhoto(mf, 2))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("This author doesn't exist!");
        Mockito.verify(authorRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void testGetAllAuthorsOfBook() {
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Picasso");
        authorOfBook.setId(1);
        authorOfBookList.add(authorOfBook);
        Mockito.when(authorRepository.findAllAuthorsOfBook()).thenReturn(authorOfBookList);
        List<AuthorOfBook> found = authorService.getAllAuthorsOfBook();
        assertThat(found).isNotEmpty();
        assertThat(found).isEqualTo(authorOfBookList);
        Mockito.verify(authorRepository, Mockito.times(1)).findAllAuthorsOfBook();
        Mockito.verifyNoMoreInteractions(authorRepository);
    }

    @Test
    public void testGetAuthorOfBookByFullname() {
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Picasso");
        authorOfBook.setId(1);
        Mockito.when(authorRepository.getAuthorOfBookByFullname(Mockito.anyString())).thenReturn(authorOfBook);
        AuthorOfBook found = authorService.getAuthorOfBookByFullname("Pablo Picasso");
        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(authorOfBook);
        Mockito.verify(authorRepository, Mockito.times(1)).getAuthorOfBookByFullname(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(authorRepository);
    }

}
