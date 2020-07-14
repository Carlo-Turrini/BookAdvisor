package com.student.book_advisor.repositories;

import com.student.book_advisor.data_persistency.model.dto.PrizeDTO;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.Prize;
import com.student.book_advisor.data_persistency.repositories.PrizeRepository;
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
public class PrizeRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PrizeRepository prizeRepository;

    Libro book = null;
    Prize prize = null;

    @Before
    public void setup() {
        book = new Libro();
        book.setTitolo("Titolo");
        book.setSinossi("sinossi");
        book.setPagine(123);
        book.setAnnoPubblicazione(1234);
        book = testEntityManager.persist(book);
        prize = new Prize();
        prize.setPrizeName("Premio di test");
        prize.setYearAwarded(2003);
        prize.setBook(book);
        prize = testEntityManager.persist(prize);
        testEntityManager.flush();
    }

    @Test
    public void testFindAllPrizesForBook() {
        List<PrizeDTO> prizeDTOList = prizeRepository.findAllPrizesForBook(book.getId());
        assertThat(prizeDTOList.size()).isEqualTo(1);
        assertThat(prizeDTOList.get(0).getId()).isEqualTo(prize.getId());
    }

    @Test
    public void testFindByIdAndBookID() {
        Prize found = prizeRepository.findByIdAndBookID(prize.getId(), book.getId());
        assertThat(found).isEqualTo(prize);
    }

    @Test
    public void testFindByBookIDAndPrizeName() {
        Prize found = prizeRepository.findByBookIDAndPrizeName(book.getId(), prize.getPrizeName());
        assertThat(found).isEqualTo(prize);
    }

    @Test
    public void testFindDTOByBookIDAndPrizeName() {
        PrizeDTO foundDTO = prizeRepository.findDTOByBookIDAndPrizeName(book.getId(), prize.getPrizeName());
        assertThat(foundDTO.getId()).isEqualTo(prize.getId());
    }
}
