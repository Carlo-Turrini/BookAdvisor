package com.student.book_advisor.entities;

import com.student.book_advisor.constants.Constants;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "UsersInfo", uniqueConstraints = {@UniqueConstraint(columnNames = {"Username"}), @UniqueConstraint(columnNames = {"Email"})})
public class UsersInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Username", length = 50, nullable = false, updatable = false)
    private String username;

    @Column(name = "Password", length = 100, nullable = false)
    private String password;

    @Column(name = "Enabled", nullable = false)
    @ColumnDefault("true")
    private Boolean enabled = true;

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

    @OneToMany(mappedBy = "usersInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Authorities> authorities;

    @OneToMany(mappedBy = "usersInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Recensione> recensioneList = new ArrayList<Recensione>();

    @OneToMany(mappedBy = "usersInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UsefulReview> usefulReviewList = new ArrayList<UsefulReview>();

    @OneToMany(mappedBy = "usersInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MyBooks> myBooksList = new ArrayList<MyBooks>();

    public Integer getId() {
        return id;
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

    public List<MyBooks> getMyBooksList() {
        return myBooksList;
    }

    public void setMyBooksList(List<MyBooks> myBooksList) {
        this.myBooksList = myBooksList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Authorities> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authorities> authorities) {
        this.authorities = authorities;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
