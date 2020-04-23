package com.student.book_advisor.entities;


import com.student.book_advisor.constants.Constants;
import com.student.book_advisor.enums.GenereLibro;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Book", uniqueConstraints = {@UniqueConstraint(columnNames = {"ISBN"})})
@Where(clause = "del_token = 00000000-0000-0000-0000-000000000000")
public class Libro implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "del_token", nullable = false)
    @ColumnDefault("0")
    private String delToken = Constants.nilUUID;

    @Column(name = "ISBN", length = 17, nullable = false)
    private String ISBN;

    @Column(name = "PubblicationYear", precision = 4, nullable = false)
    private Integer annoPubblicazione;

    @Column(name = "NumberOfPages", precision = 4, nullable = false)
    private Integer pagine;

    @Column(name = "Title", length = 64, nullable = false)
    private String titolo;

    @Column(name = "Synopsis", length = 2048, nullable = false)
    private String sinossi;

    @Column(name = "CoverPhotoPath", length = 256, nullable = false)
    @ColumnDefault(Constants.DEF_BOOK_COVER)
    private String bookCoverPath = Constants.DEF_BOOK_COVER;

    @Column(name = "Saga", nullable = false)
    @ColumnDefault("false")
    private Boolean saga = false;

    @Column(name = "SagaTitle", length = 64)
    private String titoloSaga;

    @Column(name = "NumberInSaga", precision = 2)
    private Integer numInSaga;

    @Column(name = "autori", length = 128, nullable = false)
    private String autori;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Recensione> recensioneList = new ArrayList<Recensione>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Prize> prizeList = new ArrayList<Prize>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GenreJoinBook> genreJoinBookList = new ArrayList<GenreJoinBook>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuthorJoinBook> authorJoinBookList = new ArrayList<AuthorJoinBook>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookRead> bookReadList = new ArrayList<BookRead>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookToRead> bookToReadList = new ArrayList<BookToRead>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FavouriteBook> favouriteBookList = new ArrayList<FavouriteBook>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReadingBook> readingBookList = new ArrayList<ReadingBook>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookRanking> bookRankingList = new ArrayList<BookRanking>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDelToken() {
        return delToken;
    }

    public void setDelToken(String delToken) {
        this.delToken = delToken;
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

    public String getAutori() {
        return autori;
    }

    public void setAutori(String autori) {
        this.autori = autori;
    }

    public Boolean getSaga() {
        return saga;
    }

    public void setSaga(Boolean saga) {
        this.saga = saga;
    }

    public String getTitoloSaga() {
        return titoloSaga;
    }

    public void setTitoloSaga(String titoloSaga) {
        this.titoloSaga = titoloSaga;
    }

    public Integer getNumInSaga() {
        return numInSaga;
    }

    public void setNumInSaga(Integer numInSaga) {
        this.numInSaga = numInSaga;
    }

    public List<Recensione> getRecensioneList() {
        return recensioneList;
    }

    public void setRecensioneList(List<Recensione> recensioneList) {
        this.recensioneList = recensioneList;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
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

    public List<BookRead> getBookReadList() {
        return bookReadList;
    }

    public void setBookReadList(List<BookRead> bookReadList) {
        this.bookReadList = bookReadList;
    }

    public List<BookToRead> getBookToReadList() {
        return bookToReadList;
    }

    public void setBookToReadList(List<BookToRead> bookToReadList) {
        this.bookToReadList = bookToReadList;
    }

    public List<FavouriteBook> getFavouriteBookList() {
        return favouriteBookList;
    }

    public void setFavouriteBookList(List<FavouriteBook> favouriteBookList) {
        this.favouriteBookList = favouriteBookList;
    }

    public List<ReadingBook> getReadingBookList() {
        return readingBookList;
    }

    public void setReadingBookList(List<ReadingBook> readingBookList) {
        this.readingBookList = readingBookList;
    }

    public List<BookRanking> getBookRankingList() {
        return bookRankingList;
    }

    public void setBookRankingList(List<BookRanking> bookRankingList) {
        this.bookRankingList = bookRankingList;
    }
}
