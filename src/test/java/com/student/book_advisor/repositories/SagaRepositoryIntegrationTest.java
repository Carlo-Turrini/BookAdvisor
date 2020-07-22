package com.student.book_advisor.repositories;

import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.Saga;
import com.student.book_advisor.data_persistency.repositories.SagaRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJpaTest
public class SagaRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private SagaRepository sagaRepository;

    Libro book = null;
    Saga saga = null;

    @Before
    public void setup() {
        book = new Libro();
        book.setTitolo("Title");
        book.setSinossi("sinossi");
        book.setPagine(123);
        book.setAnnoPubblicazione(1234);
        book = testEntityManager.persist(book);
        saga = new Saga();
        saga.setNumberInSaga(1);
        saga.setSagaTitle("Test saga");
        saga.setBook(book);
        saga = testEntityManager.persist(saga);
        testEntityManager.flush();
    }
    @Test
    public void testFindByBook() {
        Saga found = sagaRepository.findByBook(book);
        assertThat(found).isEqualTo(saga);
    }
}
