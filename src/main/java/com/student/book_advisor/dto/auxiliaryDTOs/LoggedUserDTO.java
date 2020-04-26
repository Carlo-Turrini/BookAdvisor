package com.student.book_advisor.dto.auxiliaryDTOs;

import com.student.book_advisor.session.LoggedUserDAO;

import java.util.Set;

public class LoggedUserDTO {
    private Long id;
    private Set<String> roles;

    public LoggedUserDTO(Long id, Set<String> roles) {
        this.id = id;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
