package com.student.book_advisor.data_persistency.model.entities;

import javax.persistence.*;

@Entity
@Table(name = "GenreJoinBook", uniqueConstraints = {@UniqueConstraint(columnNames = {"BookID", "GenreID"})})
public class GenreJoinBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GenreID")
    private Genre genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookID")
    private Libro book;

    public Integer getId() {
        return id;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Libro getBook() {
        return book;
    }

    public void setBook(Libro book) {
        this.book = book;
    }
}
