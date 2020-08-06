package com.student.book_advisor.repositories;

import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.Recensione;
import com.student.book_advisor.data_persistency.model.entities.UsefulReview;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.UsefulReviewRepository;
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
public class UsefulReviewRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UsefulReviewRepository usefulReviewRepository;

    UsersInfo usersInfo = null;
    Recensione review = null;
    UsefulReview ur = null;

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
        Libro book = new Libro();
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
        review.setLibro(book);
        review.setUsersInfo(usersInfo);
        review = testEntityManager.persist(review);
        ur = new UsefulReview();
        ur.setReview(review);
        ur.setUsersInfo(usersInfo);
        ur = testEntityManager.persist(ur);
        testEntityManager.flush();
    }

    @Test
    public void testFindByUserIDAndReviewID() {
        UsefulReview found = usefulReviewRepository.findByUserIDAndReviewID(usersInfo.getId(), review.getId());
        assertThat(found).isEqualTo(ur);
    }

    @Test
    public void testFindAllUsefulReviewsByUserID() {
        List<UsefulReview> usefulReviewList = usefulReviewRepository.findAllUsefulReviewsByUserID(usersInfo.getId());
        assertThat(usefulReviewList.size()).isEqualTo(1);
        assertThat(usefulReviewList.contains(ur)).isTrue();
    }
}
