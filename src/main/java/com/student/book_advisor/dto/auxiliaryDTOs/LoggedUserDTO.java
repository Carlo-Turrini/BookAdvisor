package com.student.book_advisor.dto.auxiliaryDTOs;

import java.util.List;

public class LoggedUserDTO {
    private Long id;
    private Boolean isAdmin;

    public LoggedUserDTO(Long id, Boolean isAdmin) {
        this.id = id;
        this.isAdmin = isAdmin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
