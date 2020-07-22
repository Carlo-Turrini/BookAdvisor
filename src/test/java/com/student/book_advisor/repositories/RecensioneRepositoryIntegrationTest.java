package com.student.book_advisor.repositories;

import com.student.book_advisor.data_persistency.model.dto.RecensioneDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.OverallRatingsForBook;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.Recensione;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.RecensioneRepository;
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
public class RecensioneRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private RecensioneRepository recensioneRepository;

    UsersInfo usersInfo = null;
    Libro book = null;
    Recensione review = null;

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
        testEntityManager.persist(book);
        review = new Recensione();
        review.setContainsSpoiler(false);
        review.setOriginalityRating(5);
        review.setPageTurnerRating(4);
        review.setWritingQualityRating(4);
        review.setRating(5);
        review.setTesto("Recensione test");
        review.setUsersInfo(usersInfo);
        review.setLibro(book);
        review = testEntityManager.persist(review);
        testEntityManager.flush();
    }

    @Test
    public void testFindAllByBook() {
        List<RecensioneDTO> recensioneDTOList = recensioneRepository.findAllByBook(book.getId());
        assertThat(recensioneDTOList).isNotEmpty();
        assertThat(recensioneDTOList.size()).isEqualTo(1);
        assertThat(recensioneDTOList.get(0).getId()).isEqualTo(review.getId());
        assertThat(recensioneDTOList.get(0).getBookId()).isEqualTo(book.getId());
    }

    @Test
    public void testFindAllByUser() {
        List<RecensioneDTO> recensioneDTOList = recensioneRepository.findAllByUser(usersInfo.getId());
        assertThat(recensioneDTOList).isNotEmpty();
        assertThat(recensioneDTOList.size()).isEqualTo(1);
        assertThat(recensioneDTOList.get(0).getId()).isEqualTo(review.getId());
        assertThat(recensioneDTOList.get(0).getUserId()).isEqualTo(usersInfo.getId());
    }

    @Test
    public void testGetAverageRatingsOfBook() {
        OverallRatingsForBook overallRatingsForBook = recensioneRepository.getAverageRatingsOfBook(book.getId());
        assertThat(overallRatingsForBook).isNotNull();
        assertThat(overallRatingsForBook.getOverallRating()).isEqualTo(new Double(review.getRating()));
        assertThat(overallRatingsForBook.getOverallOriginalityRating()).isEqualTo(new Double(review.getOriginalityRating()));
        assertThat(overallRatingsForBook.getOverallWritingQualityRating()).isEqualTo(new Double(review.getWritingQualityRating()));
        assertThat(overallRatingsForBook.getOverallPageTurnerRating()).isEqualTo(new Double(review.getPageTurnerRating()));
    }
}
