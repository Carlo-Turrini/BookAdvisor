package com.student.book_advisor.repositories;

import com.student.book_advisor.data_persistency.model.dto.BookRankingDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.BookRankRemovalInfoDTO;
import com.student.book_advisor.data_persistency.model.entities.BookRanking;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.MyBooks;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.BookRankingRepository;
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
public class BookRankingRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BookRankingRepository bookRankingRepository;

    UsersInfo usersInfo = null;
    Libro book = null;
    MyBooks myBooks = null;
    BookRanking br = null;
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
        book.setTitolo("Titolo");
        book.setSinossi("sinossi");
        book.setPagine(123);
        book.setAnnoPubblicazione(1234);
        book = testEntityManager.persist(book);
        myBooks = new MyBooks();
        myBooks.setShelfType(BookShelf.read);
        myBooks.setUsersInfo(usersInfo);
        myBooks.setBook(book);
        myBooks = testEntityManager.persist(myBooks);
        br = new BookRanking();
        br.setBookRank(1);
        br.setMyBooks(myBooks);
        br = testEntityManager.persist(br);
        testEntityManager.flush();
    }

    @Test
    public void testFindBookRankingByUser() {
        List<BookRankingDTO> bookRankingDTOList = bookRankingRepository.findBookRankingByUser(usersInfo.getId());
        assertThat(bookRankingDTOList.size()).isEqualTo(1);
        assertThat(bookRankingDTOList.get(0).getId()).isEqualTo(br.getId());
        assertThat(bookRankingDTOList.get(0).getBookID()).isEqualTo(book.getId());
    }

    @Test
    public void testFindAllByUserID() {
        List<BookRanking> bookRankingList = bookRankingRepository.findAllByUserID(usersInfo.getId());
        assertThat(bookRankingList.size()).isEqualTo(1);
        assertThat(bookRankingList.get(0).getId()).isEqualTo(br.getId());
        assertThat(bookRankingList.get(0).getBookRank()).isEqualTo(br.getBookRank());
    }

    @Test
    public void testFindAllMyBookIDsInRank() {
        List<Integer> myBooksIDList = bookRankingRepository.findAllMyBookIDsInRank(usersInfo.getId());
        assertThat(myBooksIDList.size()).isEqualTo(1);
        assertThat(myBooksIDList.get(0)).isEqualTo(myBooks.getId());
    }

    @Test
    public void testGetUsersIDFromBookRanking() {
        Integer usersID = bookRankingRepository.getUsersIDFromBookRanking(br.getId());
        assertThat(usersID).isEqualTo(usersInfo.getId());
    }

    @Test
    public void testGetBookRankingByMyBooksID() {
        BookRanking found = bookRankingRepository.getBookRankingByMyBooksID(myBooks.getId());
        assertThat(found).isEqualTo(br);
    }

    @Test
    public void testGetAllBookRanksByBookID() {
        List<BookRankRemovalInfoDTO> bookRankRemovalInfoDTOList = bookRankingRepository.getAllBookRanksByBookID(book.getId());
        assertThat(bookRankRemovalInfoDTOList.size()).isEqualTo(1);
        assertThat(bookRankRemovalInfoDTOList.get(0).getBookRankID()).isEqualTo(br.getId());
        assertThat(bookRankRemovalInfoDTOList.get(0).getUserID()).isEqualTo(usersInfo.getId());
    }
}
