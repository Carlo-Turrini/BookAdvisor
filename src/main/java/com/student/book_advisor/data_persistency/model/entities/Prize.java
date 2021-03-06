package com.student.book_advisor.data_persistency.model.entities;

import javax.persistence.*;

@Entity
@Table(name = "Prize", uniqueConstraints = {@UniqueConstraint(columnNames = {"PrizeName", "BookID"}), @UniqueConstraint(columnNames = {"YearAwarded", "PrizeName"})})
public class Prize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "YearAwarded", precision = 4, nullable = false)
    private Integer yearAwarded;

    @Column(name = "PrizeName", length = 64, nullable = false)
    private String prizeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookID")
    private Libro book;

    public Integer getId() {
        return id;
    }

    public Integer getYearAwarded() {
        return yearAwarded;
    }

    public void setYearAwarded(Integer yearAwarded) {
        this.yearAwarded = yearAwarded;
    }

    public String getPrizeName() {
        return prizeName;
    }

    public void setPrizeName(String prizeName) {
        this.prizeName = prizeName;
    }

    public Libro getBook() {
        return book;
    }

    public void setBook(Libro book) {
        this.book = book;
    }
}
