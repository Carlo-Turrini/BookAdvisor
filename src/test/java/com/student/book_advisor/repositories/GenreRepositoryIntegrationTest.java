package com.student.book_advisor.repositories;

import com.student.book_advisor.data_persistency.model.entities.Genre;
import com.student.book_advisor.data_persistency.model.entities.GenreJoinBook;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.repositories.GenreRepository;
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
public class GenreRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private GenreRepository genreRepository;

    Libro book = null;
    Genre genre = null;

    @Before
    public void setup() {
        genre = new Genre();
        genre.setGenre("Romanzo");
        genre = testEntityManager.persist(genre);
        book = new Libro();
        book.setTitolo("Titolo");
        book.setAnnoPubblicazione(1234);
        book.setPagine(123);
        book.setSinossi("sinossi");
        book = testEntityManager.persist(book);
        GenreJoinBook gjb = new GenreJoinBook();
        gjb.setBook(book);
        gjb.setGenre(genre);
        testEntityManager.persist(gjb);
        testEntityManager.flush();
    }

    @Test
    public void testFindGenresOfBook() {
        List<String> genres = genreRepository.findGenresOfBook(book.getId());
        assertThat(genres.size()).isEqualTo(1);
        assertThat(genres.get(0)).isEqualTo(genre.getGenre());
    }

    @Test
    public void testFindByGenre() {
        Genre found = genreRepository.findByGenre(genre.getGenre());
        assertThat(found).isEqualTo(genre);
    }

    @Test
    public void testFindAllToString() {
        List<String> allGenres = genreRepository.findAllToString();
        assertThat(allGenres).isNotEmpty();
        assertThat(allGenres.size()).isEqualTo(1);
        assertThat(allGenres.contains(genre.getGenre())).isTrue();
    }

    @Test
    public void testCountAllGenresByGenre() {
        Integer count = genreRepository.countAllGenresByGenre(genre.getGenre());
        assertThat(count).isEqualTo(1);
    }
}
