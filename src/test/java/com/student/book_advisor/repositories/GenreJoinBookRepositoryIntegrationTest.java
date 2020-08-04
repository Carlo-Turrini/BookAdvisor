package com.student.book_advisor.repositories;

import com.student.book_advisor.data_persistency.model.entities.Genre;
import com.student.book_advisor.data_persistency.model.entities.GenreJoinBook;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.repositories.GenreJoinBookRepository;
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
public class GenreJoinBookRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private GenreJoinBookRepository genreJoinBookRepository;

    Genre genre = null;
    Libro book = null;
    GenreJoinBook gjb = null;

    @Before
    public void setup() {
        genre = new Genre();
        genre.setGenre("Fantasy");
        genre = testEntityManager.persist(genre);
        book = new Libro();
        book.setTitolo("Title");
        book.setAnnoPubblicazione(1234);
        book.setPagine(123);
        book.setSinossi("sinossi");
        book = testEntityManager.persist(book);
        gjb = new GenreJoinBook();
        gjb.setBook(book);
        gjb.setGenre(genre);
        gjb = testEntityManager.persist(gjb);
        testEntityManager.flush();
    }
    @Test
    public void testFindByGenreAndBookID() {
        GenreJoinBook genreJoinBook = genreJoinBookRepository.findByGenreAndBookID(genre.getGenre(), book.getId());
        assertThat(genreJoinBook).isEqualTo(gjb);
    }
}
