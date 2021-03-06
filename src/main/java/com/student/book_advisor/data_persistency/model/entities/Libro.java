package com.student.book_advisor.data_persistency.model.entities;


import com.student.book_advisor.services.storage.Constants;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Book", uniqueConstraints = {@UniqueConstraint(columnNames = {"Title"})})
public class Libro implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "PubblicationYear", precision = 4, nullable = false)
    private Integer annoPubblicazione;

    @Column(name = "NumberOfPages", precision = 4, nullable = false)
    private Integer pagine;

    @Column(name = "Title", length = 64, nullable = false)
    private String titolo;

    @Column(name = "Synopsis", length = 2048, nullable = false)
    private String sinossi;

    @Column(name = "CoverPhotoPath", length = 256, nullable = false, columnDefinition = "varchar(256) default '/assets/images/default-cover.png'")
    private String bookCoverPath = Constants.DEF_BOOK_COVER;

    @OneToOne(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Saga saga;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Recensione> recensioneList = new ArrayList<Recensione>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Prize> prizeList = new ArrayList<Prize>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GenreJoinBook> genreJoinBookList = new ArrayList<GenreJoinBook>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuthorJoinBook> authorJoinBookList = new ArrayList<AuthorJoinBook>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MyBooks> myBooksList = new ArrayList<MyBooks>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public void setAnnoPubblicazione(Integer annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }

    public Integer getPagine() {
        return pagine;
    }

    public void setPagine(Integer pagine) {
        this.pagine = pagine;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getSinossi() {
        return sinossi;
    }

    public void setSinossi(String sinossi) {
        this.sinossi = sinossi;
    }

   public String getBookCoverPath() {
        return bookCoverPath;
    }

    public void setBookCoverPath(String bookCoverPath) {
        this.bookCoverPath = bookCoverPath;
    }

    public Saga getSaga() {
        return saga;
    }

    public void setSaga(Saga saga) {
        this.saga = saga;
    }

    public List<Recensione> getRecensioneList() {
        return recensioneList;
    }

    public void setRecensioneList(List<Recensione> recensioneList) {
        this.recensioneList = recensioneList;
    }

    public List<Prize> getPrizeList() {
        return prizeList;
    }

    public void setPrizeList(List<Prize> prizeList) {
        this.prizeList = prizeList;
    }

    public List<GenreJoinBook> getGenreJoinBookList() {
        return genreJoinBookList;
    }

    public void setGenreJoinBookList(List<GenreJoinBook> genreJoinBookList) {
        this.genreJoinBookList = genreJoinBookList;
    }

    public List<AuthorJoinBook> getAuthorJoinBookList() {
        return authorJoinBookList;
    }

    public void setAuthorJoinBookList(List<AuthorJoinBook> authorJoinBookList) {
        this.authorJoinBookList = authorJoinBookList;
    }

    public List<MyBooks> getMyBooksList() {
        return myBooksList;
    }

    public void setMyBooksList(List<MyBooks> myBooksList) {
        this.myBooksList = myBooksList;
    }

}
