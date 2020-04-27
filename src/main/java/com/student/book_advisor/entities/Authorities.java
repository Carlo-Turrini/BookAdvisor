package com.student.book_advisor.entities;

import javax.persistence.*;

@Entity
@Table(name = "Authorities", uniqueConstraints = {@UniqueConstraint(columnNames = {"Authority", "UserID"})})
public class Authorities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Authority", length = 50, nullable = false)
    private String authority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private UsersInfo usersInfo;


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
