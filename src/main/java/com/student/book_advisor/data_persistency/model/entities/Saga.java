package com.student.book_advisor.data_persistency.model.entities;

import javax.persistence.*;

@Entity
@Table(name = "Saga", uniqueConstraints = {@UniqueConstraint(columnNames = {"NumberInSaga", "SagaTitle"}), @UniqueConstraint(columnNames = {"BookID"})})
public class Saga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "SagaTitle", length = 64, nullable = false)
    private String sagaTitle;

    @Column(name = "NumberInSaga", precision = 2, nullable = false)
    private Integer numberInSaga;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookID")
    private Libro book;

    public Integer getId() {
        return id;
    }

    public String getSagaTitle() {
        return sagaTitle;
    }

    public void setSagaTitle(String sagaTitle) {
        this.sagaTitle = sagaTitle;
    }

    public Integer getNumberInSaga() {
        return numberInSaga;
    }

    public void setNumberInSaga(Integer numberInSaga) {
        this.numberInSaga = numberInSaga;
    }

    public Libro getBook() {
        return book;
    }

    public void setBook(Libro book) {
        this.book = book;
    }
}
