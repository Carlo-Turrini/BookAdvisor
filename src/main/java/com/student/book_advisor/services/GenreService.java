package com.student.book_advisor.services;

import java.util.List;

public interface GenreService {

    public void addGenre(String genre);
    public List<String> getAllGenres();
    public Boolean isGenreUnique(String genre);
}
