package com.student.book_advisor.entities;

import com.student.book_advisor.constants.Constants;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Author", uniqueConstraints = {@UniqueConstraint(columnNames = {"AuthorsFullname"})})
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "AuthorsFullname", length = 50, nullable = false)
    private String authorsFullname;

    @Column(name = "BirthYear", precision = 4, nullable =  false)
    private Integer birthYear;

    @Column(name = "DeathYear", precision = 4)
    private Integer deathYear;

    @Column(name = "Bibliography", length = 4000, nullable = false)
    private String bibliography;

    @Column(name = "AuthorsPhotoPath", length = 256, nullable = false)
    @ColumnDefault(Constants.DEF_PROFILE_PIC)
    private String authorsPhotoPath = Constants.DEF_PROFILE_PIC;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AuthorJoinBook> authorJoinBookList = new ArrayList<AuthorJoinBook>();

    public Long getId() {
        return id;
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

    public String getBibliography() {
        return bibliography;
    }

    public void setBibliography(String bibliography) {
        this.bibliography = bibliography;
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
