package com.student.book_advisor.data_persistency.model.entities;

import com.student.book_advisor.enums.BookShelf;

import javax.persistence.*;

@Entity
@Table(name = "MyBooks", uniqueConstraints = {@UniqueConstraint(columnNames = {"UserID", "BookID"})})
public class MyBooks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ShelfType", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookShelf ShelfType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private UsersInfo usersInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookID")
    private Libro book;

    @OneToOne(mappedBy = "myBooks", fetch = FetchType.LAZY)
    private BookRanking bookRanking;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
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

    public BookRanking getBookRanking() {
        return bookRanking;
    }

    public void setBookRanking(BookRanking bookRanking) {
        this.bookRanking = bookRanking;
    }
}
