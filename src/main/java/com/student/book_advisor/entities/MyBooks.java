package com.student.book_advisor.entities;

import com.student.book_advisor.enums.BookShelf;

import javax.persistence.*;

@Entity
@Table(name = "MyBooks", uniqueConstraints = {@UniqueConstraint(columnNames = {"UserID", "BookID"})})
public class MyBooks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ShelfType", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookShelf ShelfType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private UsersInfo usersInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookID")
    private Libro book;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BookShelf getShelfType() {
        return ShelfType;
    }

    public void setShelfType(BookShelf shelfType) {
        ShelfType = shelfType;
    }

    public UsersInfo getUsersInfo() {
        return usersInfo;
    }

    public void setUsersInfo(UsersInfo usersInfo) {
        this.usersInfo = usersInfo;
    }

    public Libro getBook() {
        return book;
    }

    public void setBook(Libro book) {
        this.book = book;
    }

}
