package com.student.book_advisor.data_persistency.model.entities;

import javax.persistence.*;

@Entity
@Table(name = "UsefulReview", uniqueConstraints = {@UniqueConstraint(columnNames = {"UserID", "ReviewID"})})
public class UsefulReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReviewID")
    private Recensione review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private UsersInfo usersInfo;

    public Integer getId() {
        return id;
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
