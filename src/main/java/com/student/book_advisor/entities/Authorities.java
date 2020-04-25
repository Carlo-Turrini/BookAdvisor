package com.student.book_advisor.entities;

import com.student.book_advisor.entities.idClass.AuthoritiesId;

import javax.persistence.*;

@Entity
@IdClass(AuthoritiesId.class)
@Table(name = "Authorities")
public class Authorities {
    @Id
    @Column(name = "UserID", length = 50, nullable = false)
    private Long userID;

    @Id
    @Column(name = "Authority", length = 50, nullable = false)
    private String authority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private UsersInfo usersInfo;

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public UsersInfo getUsersInfo() {
        return usersInfo;
    }

    public void setUsersInfo(UsersInfo usersInfo) {
        this.usersInfo = usersInfo;
    }
}
