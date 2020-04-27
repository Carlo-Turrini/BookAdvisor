package com.student.book_advisor.entities;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Table(name = "ReadingProgress", uniqueConstraints = {@UniqueConstraint(columnNames = {"MyBooksID"})})
public class ReadingProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PagesRead", precision = 4, nullable = false)
    private Integer pagesRead;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MyBooksID")
    private MyBooks myBook;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPagesRead() {
        return pagesRead;
    }

    public void setPagesRead(Integer pagesRead) {
        this.pagesRead = pagesRead;
    }

    public MyBooks getMyBook() {
        return myBook;
    }

    public void setMyBook(MyBooks myBook) {
        this.myBook = myBook;
    }
}
