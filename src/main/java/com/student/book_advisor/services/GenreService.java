package com.student.book_advisor.services;

import java.util.List;

public interface GenreService {

    public List<String> addGenre(String genre);
    public List<String> getAllGenres();
}
