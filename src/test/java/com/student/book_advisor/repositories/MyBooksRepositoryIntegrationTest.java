package com.student.book_advisor.repositories;

import com.student.book_advisor.data_persistency.model.dto.MyBooksDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.MyBooksReadDTO;
import com.student.book_advisor.data_persistency.model.entities.BookRanking;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.MyBooks;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.MyBooksRepository;
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
public class MyBooksRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private MyBooksRepository myBooksRepository;

    UsersInfo usersInfo = null;
    Libro book = null;
    MyBooks myBooks = null;

    @Before
    public void setup() {
        usersInfo = new UsersInfo();
        usersInfo.setUsername("Username");
        usersInfo.setPassword("password");
        usersInfo.setName("Mario");
        usersInfo.setSurname("Rossi");
        usersInfo.setEmail("mario.rossi@mail.it");
        usersInfo.setDescription("Test");
        usersInfo = testEntityManager.persist(usersInfo);
        book = new Libro();
        book.setTitolo("Title");
        book.setSinossi("sinossi");
        book.setPagine(123);
        book.setAnnoPubblicazione(1234);
        book = testEntityManager.persist(book);
        myBooks = new MyBooks();
        myBooks.setShelfType(BookShelf.read);
        myBooks.setUsersInfo(usersInfo);
        myBooks.setBook(book);
        myBooks = testEntityManager.persist(myBooks);
        testEntityManager.flush();
    }

    @Test
    public void testGetMyBooksByUserID() {
        List<MyBooksDTO> myBooksDTOList = myBooksRepository.getMyBooksByUserID(usersInfo.getId());
        assertThat(myBooksDTOList.size()).isEqualTo(1);
        assertThat(myBooksDTOList.get(0).getId()).isEqualTo(myBooks.getId());
    }

    @Test
    public void testGetByIdAndUserId() {
        MyBooks found = myBooksRepository.getByIdAndUserId(myBooks.getId(), usersInfo.getId());
        assertThat(found).isEqualTo(myBooks);
    }

    @Test
    public void testGetByBookIDAndUserID() {
        MyBooks found = myBooksRepository.getByBookIDAndUserID(book.getId(), usersInfo.getId());
        assertThat(found).isEqualTo(myBooks);
    }

    @Test
    public void testGetBookShelfByBookIDAndUserID() {
        BookShelf bookShelf = myBooksRepository.getBookShelfByBookIDAndUserID(book.getId(), usersInfo.getId());
        assertThat(bookShelf).isEqualByComparingTo(myBooks.getShelfType());
    }

    @Test
    public void testGetAllMyBooksReadButNotInRank() {
        List<MyBooksReadDTO> myBooksReadDTOList = myBooksRepository.getAllMyBooksReadButNotInRank(usersInfo.getId());
        assertThat(myBooksReadDTOList.size()).isEqualTo(1);
        assertThat(myBooksReadDTOList.get(0).getMyBooksID()).isEqualTo(myBooks.getId());
        BookRanking br = new BookRanking();
        br.setBookRank(1);
        br.setMyBooks(myBooks);
        br = testEntityManager.persist(br);
        testEntityManager.flush();
        myBooksReadDTOList = myBooksRepository.getAllMyBooksReadButNotInRank(usersInfo.getId());
        assertThat(myBooksReadDTOList).isEmpty();
    }
}
