package com.student.book_advisor.services;

import com.student.book_advisor.data_persistency.model.entities.Genre;
import com.student.book_advisor.data_persistency.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService{
    @Autowired
    private GenreRepository genreRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGenre(String genre) {
        if(genreRepository.findByGenre(genre) == null) {
            Genre gen = new Genre();
            gen.setGenre(genre);
            genreRepository.save(gen);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<String> getAllGenres() {
        return genreRepository.findAllToString();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean isGenreUnique(String genre) {
        return !(genreRepository.countAllGenresByGenre(genre) > 0);
    }
}
