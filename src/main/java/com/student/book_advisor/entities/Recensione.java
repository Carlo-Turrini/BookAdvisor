package com.student.book_advisor.entities;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Review", uniqueConstraints = {@UniqueConstraint(columnNames = {"UserID", "BookID"})})
@SQLDelete(sql = "UPDATE RECENSIONE SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Recensione implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "text", length = 2048, nullable = false)
    private String testo;

    @Column(name = "OverallRating", precision = 2, nullable = false)
    @ColumnDefault("3")
    private Integer rating = 3;

    @Column(name = "WritingQualityRating", precision = 2, nullable = false)
    @ColumnDefault("3")
    private Integer writingQualityRating = 3;

    @Column(name = "OriginalityRating", precision = 2, nullable = false)
    @ColumnDefault("3")
    private Integer originalityRating = 3;

    @Column(name = "PageTurnerRating", precision = 2, nullable = false)
    @ColumnDefault("3")
    private Integer pageTurnerRating = 3;

    @Column(name = "ContainsSpoiler", nullable = false)
    @ColumnDefault("false")
    private Boolean containsSpoiler = false;

    @Column(name = "CreationDate", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp timestamp;

    @Column(name = "deleted", nullable = false)
    @ColumnDefault("false")
    private Boolean deleted = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookID")
    private Libro libro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private UsersInfo usersInfo;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> commentList = new ArrayList<Comment>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UsefulReview> usefulReviewList = new ArrayList<UsefulReview>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getWritingQualityRating() {
        return writingQualityRating;
    }

    public void setWritingQualityRating(Integer writingQualityRating) {
        this.writingQualityRating = writingQualityRating;
    }

    public Integer getOriginalityRating() {
        return originalityRating;
    }

    public void setOriginalityRating(Integer originalityRating) {
        this.originalityRating = originalityRating;
    }

    public Integer getPageTurnerRating() {
        return pageTurnerRating;
    }

    public void setPageTurnerRating(Integer pageTurnerRating) {
        this.pageTurnerRating = pageTurnerRating;
    }

    public Boolean getContainsSpoiler() {
        return containsSpoiler;
    }

    public void setContainsSpoiler(Boolean containsSpoiler) {
        this.containsSpoiler = containsSpoiler;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public UsersInfo getUsersInfo() {
        return usersInfo;
    }

    public void setUsersInfo(UsersInfo usersInfo) {
        this.usersInfo = usersInfo;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public List<UsefulReview> getUsefulReviewList() {
        return usefulReviewList;
    }

    public void setUsefulReviewList(List<UsefulReview> usefulReviewList) {
        this.usefulReviewList = usefulReviewList;
    }

}
