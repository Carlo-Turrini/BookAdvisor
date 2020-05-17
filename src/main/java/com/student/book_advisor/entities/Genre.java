package com.student.book_advisor.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Genre", uniqueConstraints = {@UniqueConstraint(columnNames = {"Genre"})})
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Genre", length = 20, nullable = false)
    private String genre;

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GenreJoinBook> genreJoinBookList = new ArrayList<GenreJoinBook>();

    public Integer getId() {
        return id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public List<GenreJoinBook> getGenreJoinBookList() {
        return genreJoinBookList;
    }

    public void setGenreJoinBookList(List<GenreJoinBook> genreJoinBookList) {
        this.genreJoinBookList = genreJoinBookList;
    }
}
