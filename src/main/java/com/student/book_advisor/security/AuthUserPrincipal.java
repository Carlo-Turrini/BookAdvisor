package com.student.book_advisor.security;

import com.student.book_advisor.entities.UsersInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class AuthUserPrincipal implements UserDetails {
    private UsersInfo usersInfo;
    private Set<SimpleGrantedAuthority> authorities;

    public AuthUserPrincipal(UsersInfo usersInfo, Set<SimpleGrantedAuthority> authorities) {
        this.usersInfo = usersInfo;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.usersInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return this.usersInfo.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.usersInfo.getEnabled();
    }

    public UsersInfo getUsersInfo() {
        return this.usersInfo;
    }

    public Long getId() {return this.usersInfo.getId();}
}
