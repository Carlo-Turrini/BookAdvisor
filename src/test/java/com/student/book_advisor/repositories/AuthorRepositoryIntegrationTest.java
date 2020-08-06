package com.student.book_advisor.repositories;

import com.student.book_advisor.data_persistency.model.dto.AuthorCardDTO;
import com.student.book_advisor.data_persistency.model.dto.AuthorDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.data_persistency.model.entities.*;
import com.student.book_advisor.data_persistency.repositories.AuthorRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AuthorRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AuthorRepository authorRepository;

    Author author = null;

    @Before
    public void setup() {
        author = new Author();
        author.setAuthorsFullname("Giacomo Leopardi");
        author.setAuthorsPhotoPath("/default");
        author.setBiography("Test");
        author.setBirthYear(1923);
        author.setDeathYear(2015);
        testEntityManager.persist(author);
        testEntityManager.flush();
    }

    @Test
    public void testGetAuthorsDTOById() {
        AuthorDTO authorDTO = authorRepository.getAuthorsDTOById(author.getId());
        assertThat(authorDTO).isNotNull();
        assertThat(authorDTO.getId()).isEqualTo(author.getId());
        assertThat(authorDTO.getAuthorsFullname()).isEqualTo(author.getAuthorsFullname());
    }

    @Test
    public void testFindAllToDTO() {
        List<AuthorCardDTO> authorCardDTOList = authorRepository.findAllToDTO();
        assertThat(authorCardDTOList).isNotEmpty();
        assertThat(authorCardDTOList.size()).isGreaterThanOrEqualTo(4);
        assertThat(authorCardDTOList.get(authorCardDTOList.size()-1).getAuthorsFullname()).isEqualTo(author.getAuthorsFullname());
    }

    @Test
    public void testFindAuthorsOfBook() {
        AuthorJoinBook ajb = new AuthorJoinBook();
        Libro book = new Libro();
        book.setTitolo("Test");
        book.setAnnoPubblicazione(2003);
        book.setPagine(345);
        book.setSinossi("Test");
        book = testEntityManager.persist(book);
        ajb.setAuthor(author);
        ajb.setBook(book);
        testEntityManager.persist(ajb);
        testEntityManager.flush();
        List<AuthorOfBook> authorOfBookList = authorRepository.findAuthorsOfBook(book.getId());
        assertThat(authorOfBookList.size()).isEqualTo(1);
        assertThat(authorOfBookList.get(0).getId()).isEqualTo(author.getId());
        assertThat(authorOfBookList.get(0).getAuthorsFullname()).isEqualTo(author.getAuthorsFullname());
    }

    @Test
    public void testCountAllAuthorsWithName() {
        Integer count = authorRepository.countAllAuthorsWithName(author.getAuthorsFullname());
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testFindAllAuthorsOfBook() {
        List<AuthorOfBook> authorOfBookList = authorRepository.findAllAuthorsOfBook();
        assertThat(authorOfBookList.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void testGetAuthorOfBookByFullname() {
        AuthorOfBook authorOfBook = authorRepository.getAuthorOfBookByFullname(author.getAuthorsFullname());
        assertThat(authorOfBook).isNotNull();
        assertThat(authorOfBook.getAuthorsFullname()).isEqualTo(author.getAuthorsFullname());
        assertThat(authorOfBook.getId()).isEqualTo(author.getId());
    }

    @Test
    public void testCountAllBooksByAuthor() {
        Integer count = authorRepository.countAllBooksByAuthor(author.getId());
        assertThat(count).isNotNull();
        assertThat(count).isEqualTo(0);
    }
}
