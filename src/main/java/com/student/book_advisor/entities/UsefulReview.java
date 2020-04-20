package com.student.book_advisor.entities;

import javax.persistence.*;

@Entity
@Table(name = "UsefulReview", uniqueConstraints = {@UniqueConstraint(columnNames = {"UserID", "ReviewID"})})
public class UsefulReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Useful", nullable = false)
    private Boolean useful;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReviewID")
    private Recensione review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private UsersInfo usersInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getUseful() {
        return useful;
    }

    public void setUseful(Boolean useful) {
        this.useful = useful;
    }

    public Recensione getReview() {
        return review;
    }

    public void setReview(Recensione review) {
        this.review = review;
    }

    public UsersInfo getUsersInfo() {
        return usersInfo;
    }

    public void setUsersInfo(UsersInfo usersInfo) {
        this.usersInfo = usersInfo;
    }
}
