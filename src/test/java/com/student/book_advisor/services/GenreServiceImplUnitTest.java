package com.student.book_advisor.services;

import com.student.book_advisor.data_persistency.model.entities.Genre;
import com.student.book_advisor.data_persistency.repositories.GenreRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class GenreServiceImplUnitTest {

    @TestConfiguration
    static class GenreServiceImplTestContextConfiguration {
        @Bean
        public GenreService genreService() {
            return new GenreServiceImpl();
        }
    }

    @Autowired
    private GenreService genreService;

    @MockBean
    private GenreRepository genreRepository;

    @Test
    public void testAddGenre() {
        Genre genre = new Genre();
        genre.setGenre("Romanzo");
        Mockito.when(genreRepository.findByGenre(Mockito.anyString())).thenReturn(null);
        Mockito.when(genreRepository.save(Mockito.any(Genre.class))).thenReturn(genre);
        genreService.addGenre("Romanzo");
        Mockito.verify(genreRepository, Mockito.times(1)).findByGenre(Mockito.anyString());
        Mockito.verify(genreRepository, Mockito.times(1)).save(Mockito.any(Genre.class));
        Mockito.verifyNoMoreInteractions(genreRepository);
    }

    @Test
    public void testAddGenre_genreAlreadyPresent() {
        Genre genre = new Genre();
        genre.setGenre("Romanzo");
        Mockito.when(genreRepository.findByGenre(Mockito.anyString())).thenReturn(genre);
        genreService.addGenre("Romanzo");
        Mockito.verify(genreRepository, Mockito.times(1)).findByGenre(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(genreRepository);

    }

    @Test
    public void testGetAllGenres() {
        List<String> genres = new ArrayList<>();
        genres.add("Romanzo");
        genres.add("Fantasy");
        Mockito.when(genreRepository.findAllToString()).thenReturn(genres);
        List<String> found = genreService.getAllGenres();
        assertThat(found).isNotEmpty();
        assertThat(found).isEqualTo(genres);
        Mockito.verify(genreRepository, Mockito.times(1)).findAllToString();
        Mockito.verifyNoMoreInteractions(genreRepository);
    }

    @Test
    public void testIsGenreUnique_True() {
        Mockito.when(genreRepository.countAllGenresByGenre(Mockito.anyString())).thenReturn(0);
        Boolean unique = genreService.isGenreUnique("Romanzo");
        assertThat(unique).isTrue();
        Mockito.verify(genreRepository, Mockito.times(1)).countAllGenresByGenre(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(genreRepository);
    }

    @Test
    public void testIsGenreUnique_False() {
        Mockito.when(genreRepository.countAllGenresByGenre(Mockito.anyString())).thenReturn(1);
        Boolean unique = genreService.isGenreUnique("Romanzo");
        assertThat(unique).isFalse();
        Mockito.verify(genreRepository, Mockito.times(1)).countAllGenresByGenre(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(genreRepository);
    }
}
