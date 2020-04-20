package com.student.book_advisor.entities;

import com.student.book_advisor.constants.Constants;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "UsersInfo", uniqueConstraints = {@UniqueConstraint(columnNames = {"Username"}), @UniqueConstraint(columnNames = {"Email"})})
public class UsersInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Name", length = 20, nullable = false)
    private String name;

    @Column(name = "Surname", length = 20, nullable = false)
    private String surname;

    @Column(name = "Email", length = 64, nullable = false)
    @Email
    private String email;

    @Column(name = "Description", length = 1024, nullable = false)
    private String description;

    @Column(name = "ProfilePhotoPath", length = 256, nullable = false)
    @ColumnDefault(Constants.DEF_PROFILE_PIC)
    private String profilePhotoPath = Constants.DEF_PROFILE_PIC;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Username")
    private Users user;

    @OneToMany(mappedBy = "usersInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookRanking> bookRankingList = new ArrayList<BookRanking>();

    @OneToMany(mappedBy = "usersInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookRead> bookReadList = new ArrayList<BookRead>();

    @OneToMany(mappedBy = "usersInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookToRead> bookToReadList = new ArrayList<BookToRead>();

    @OneToMany(mappedBy = "usersInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> commentList = new ArrayList<Comment>();

    @OneToMany(mappedBy = "usersInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FavouriteBook> favouriteBookList = new ArrayList<FavouriteBook>();

    @OneToMany(mappedBy = "usersInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReadingBook> readingBookList = new ArrayList<ReadingBook>();

    @OneToMany(mappedBy = "usersInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Recensione> recensioneList = new ArrayList<Recensione>();

    @OneToMany(mappedBy = "usersInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UsefulReview> usefulReviewList = new ArrayList<UsefulReview>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public List<BookRanking> getBookRankingList() {
        return bookRankingList;
    }

    public void setBookRankingList(List<BookRanking> bookRankingList) {
        this.bookRankingList = bookRankingList;
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

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
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

    public List<Recensione> getRecensioneList() {
        return recensioneList;
    }

    public void setRecensioneList(List<Recensione> recensioneList) {
        this.recensioneList = recensioneList;
    }

    public List<UsefulReview> getUsefulReviewList() {
        return usefulReviewList;
    }

    public void setUsefulReviewList(List<UsefulReview> usefulReviewList) {
        this.usefulReviewList = usefulReviewList;
    }
}
