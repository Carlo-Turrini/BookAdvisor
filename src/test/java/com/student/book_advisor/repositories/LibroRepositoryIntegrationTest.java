package com.student.book_advisor.repositories;

import com.student.book_advisor.data_persistency.model.dto.LibroCardDTO;
import com.student.book_advisor.data_persistency.model.dto.LibroDTO;
import com.student.book_advisor.data_persistency.model.entities.*;
import com.student.book_advisor.data_persistency.repositories.LibroRepository;
import com.student.book_advisor.enums.BookShelf;
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
public class LibroRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private LibroRepository libroRepository;

    Author author = null;
    Libro book = null;
    Genre genre = null;
    Saga saga = null;
    MyBooks myBooks = null;

    @Before
    public void setup() {
        book = new Libro();
        book.setTitolo("Test");
        book.setAnnoPubblicazione(2003);
        book.setPagine(345);
        book.setSinossi("Test");
        book = testEntityManager.persist(book);
        author = new Author();
        author.setAuthorsFullname("Mario Rossi");
        author.setBirthYear(1987);
        author.setBiography("Test");
        author = testEntityManager.persist(author);
        AuthorJoinBook ajb = new AuthorJoinBook();
        ajb.setAuthor(author);
        ajb.setBook(book);
        testEntityManager.persist(ajb);
        genre = new Genre();
        genre.setGenre("Fantasy");
        testEntityManager.persist(genre);
        GenreJoinBook gjb = new GenreJoinBook();
        gjb.setGenre(genre);
        gjb.setBook(book);
        testEntityManager.persist(gjb);
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setUsername("Username");
        usersInfo.setPassword("password");
        usersInfo.setName("Mario");
        usersInfo.setSurname("Rossi");
        usersInfo.setEmail("mario.rossi@mail.it");
        usersInfo.setDescription("Test");
        usersInfo = testEntityManager.persist(usersInfo);
        myBooks = new MyBooks();
        myBooks.setShelfType(BookShelf.read);
        myBooks.setUsersInfo(usersInfo);
        myBooks.setBook(book);
        myBooks = testEntityManager.persist(myBooks);
        saga = new Saga();
        saga.setSagaTitle("Test saga");
        saga.setNumberInSaga(1);
        saga.setBook(book);
        testEntityManager.persist(saga);
        Recensione review = new Recensione();
        review.setContainsSpoiler(false);
        review.setOriginalityRating(5);
        review.setPageTurnerRating(4);
        review.setWritingQualityRating(4);
        review.setRating(5);
        review.setTesto("Recensione test");
        review.setUsersInfo(usersInfo);
        review.setLibro(book);
        testEntityManager.flush();
    }

    @Test
    public void testFindLibriByGenere() {
        List<LibroCardDTO> libroCardDTOList = libroRepository.findLibriByGenere(genre.getGenre());
        assertThat(libroCardDTOList).isNotEmpty();
        assertThat(libroCardDTOList.size()).isEqualTo(1);
        assertThat(libroCardDTOList.get(0).getId()).isEqualTo(book.getId());
    }

    @Test
    public void testFindAllBooks() {
        List<LibroCardDTO> libroCardDTOList = libroRepository.findAllBooks();
        assertThat(libroCardDTOList).isNotEmpty();
        assertThat(libroCardDTOList.size()).isGreaterThanOrEqualTo(1);
        assertThat(libroCardDTOList.get(libroCardDTOList.size()-1).getId()).isEqualTo(book.getId());
    }

    @Test
    public void testFindBookCoverPath() {
        String bookCoverPath = libroRepository.findBookCoverPath(book.getId());
        assertThat(bookCoverPath).isNotNull();
        assertThat(bookCoverPath).isEqualTo(book.getBookCoverPath());
    }

    @Test
    public void testCountAllByTitolo() {
        Integer count = libroRepository.countAllByTitolo(book.getTitolo());
        assertThat(count).isNotNull();
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testFindAllBooksContainingTitolo() {
        List<LibroCardDTO> libroCardDTOList = libroRepository.findAllBooksContainingTitolo(book.getTitolo());
        assertThat(libroCardDTOList).isNotEmpty();
        assertThat(libroCardDTOList.size()).isEqualTo(1);
        assertThat(libroCardDTOList.get(0).getId()).isEqualTo(book.getId());
    }

    @Test
    public void testGetBookById() {
        LibroDTO libroDTO = libroRepository.getBookById(book.getId());
        assertThat(libroDTO).isNotNull();
        assertThat(libroDTO.getId()).isEqualTo(book.getId());
    }

    @Test
    public void testFindAllBooksByTitoloSagaExcludingCurrent() {
        List<LibroCardDTO> libroCardDTOList = libroRepository.findAllBooksByTitoloSagaExcludingCurrent(saga.getSagaTitle(), book.getId());
        assertThat(libroCardDTOList).isEmpty();
        Libro book2 = new Libro();
        book2.setSinossi("Prova2");
        book2.setPagine(123);
        book2.setAnnoPubblicazione(1234);
        book2.setTitolo("Prova2");
        book2 = testEntityManager.persist(book2);
        Saga saga2 = new Saga();
        saga2.setSagaTitle(saga.getSagaTitle());
        saga2.setNumberInSaga(saga.getNumberInSaga()+1);
        saga2.setBook(book2);
        testEntityManager.persist(saga2);
        testEntityManager.flush();
        libroCardDTOList = libroRepository.findAllBooksByTitoloSagaExcludingCurrent(saga.getSagaTitle(), book.getId());
        assertThat(libroCardDTOList).isNotEmpty();
        assertThat(libroCardDTOList.size()).isEqualTo(1);
        assertThat(libroCardDTOList.get(0).getId()).isEqualTo(book2.getId());
    }

    @Test
    public void testFindAllBooksByAuthor() {
        List<LibroCardDTO> libroCardDTOList = libroRepository.findAllBooksByAuthor(author.getAuthorsFullname());
        assertThat(libroCardDTOList).isNotEmpty();
        assertThat(libroCardDTOList.size()).isEqualTo(1);
        assertThat(libroCardDTOList.get(0).getId()).isEqualTo(book.getId());
    }

    @Test
    public void testGetByMyBooksID() {
        Libro found = libroRepository.getByMyBooksID(myBooks.getId());
        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(book);
    }


}
