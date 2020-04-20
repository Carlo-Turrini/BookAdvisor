package com.student.book_advisor.entities;

import com.student.book_advisor.entities.idClass.AuthoritiesId;

import javax.persistence.*;

@Entity
@IdClass(AuthoritiesId.class)
@Table(name = "Authorities")
public class Authorities {
    @Id
    @Column(name = "Username", length = 50, nullable = false)
    private String username;

    @Id
    @Column(name = "Authority", length = 50, nullable = false)
    private String authority;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
