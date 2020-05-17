package com.student.book_advisor.entities;

import javax.persistence.*;

@Entity
@Table(name = "AuthorJoinBook", uniqueConstraints = {@UniqueConstraint(columnNames = {"AuthorID", "BookID"})})
public class AuthorJoinBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AuthorID")
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookID")
    private Libro book;

    public Integer getId() {
        return id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Libro getBook() {
        return book;
    }

    public void setBook(Libro book) {
        this.book = book;
    }
}
