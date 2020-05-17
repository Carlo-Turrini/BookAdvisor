package com.student.book_advisor.dto.auxiliaryDTOs;

import java.util.List;

public class LoggedUserDTO {
    private Integer id;
    private Boolean isAdmin;

    public LoggedUserDTO(Integer id, Boolean isAdmin) {
        this.id = id;
        this.isAdmin = isAdmin;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
