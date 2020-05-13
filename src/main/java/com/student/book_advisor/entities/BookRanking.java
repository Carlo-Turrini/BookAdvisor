package com.student.book_advisor.entities;

import javax.persistence.*;

@Entity
@Table(name = "BookRanking", uniqueConstraints = {@UniqueConstraint(columnNames = {"MyBooksID"})})
public class BookRanking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BookRank", precision = 2, nullable = false)
    private Integer bookRank;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MyBooksID")
    private MyBooks myBooks;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBookRank() {
        return bookRank;
    }

    public void setBookRank(Integer bookRank) {
        this.bookRank = bookRank;
    }

    public MyBooks getMyBooks() {
        return myBooks;
    }

    public void setMyBooks(MyBooks myBooks) {
        this.myBooks = myBooks;
    }
}
