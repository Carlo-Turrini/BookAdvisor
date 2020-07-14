package com.student.book_advisor.data_persistency.model.entities;

import com.student.book_advisor.services.storage.Constants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Author", uniqueConstraints = {@UniqueConstraint(columnNames = {"AuthorsFullname"})})
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "AuthorsFullname", length = 50, nullable = false)
    private String authorsFullname;

    @Column(name = "BirthYear", precision = 4, nullable =  false)
    private Integer birthYear;

    @Column(name = "DeathYear", precision = 4)
    private Integer deathYear;

    @Column(name = "Biography", length = 4000, nullable = false)
    private String biography;


    @Column(name = "AuthorsPhotoPath", length = 256, nullable = false, columnDefinition = "varchar(256) default '/assets/images/default-profile.jpg'")
    private String authorsPhotoPath = Constants.DEF_PROFILE_PIC;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuthorJoinBook> authorJoinBookList = new ArrayList<AuthorJoinBook>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getAuthorsPhotoPath() {
        return authorsPhotoPath;
    }

    public void setAuthorsPhotoPath(String authorsPhotoPath) {
        this.authorsPhotoPath = authorsPhotoPath;
    }

    public List<AuthorJoinBook> getAuthorJoinBookList() {
        return authorJoinBookList;
    }

    public void setAuthorJoinBookList(List<AuthorJoinBook> authorJoinBookList) {
        this.authorJoinBookList = authorJoinBookList;
    }

    public String getAuthorsFullname() {
        return authorsFullname;
    }

    public void setAuthorsFullname(String authorsFullname) {
        this.authorsFullname = authorsFullname;
    }
}
