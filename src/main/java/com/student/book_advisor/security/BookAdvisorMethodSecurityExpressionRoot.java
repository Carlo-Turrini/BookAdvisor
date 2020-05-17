package com.student.book_advisor.security;

import com.student.book_advisor.entityRepositories.RecensioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class BookAdvisorMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private Object filterObject;
    private Object returnObject;

    @Autowired
    private RecensioneRepository recensioneRepository;

    public BookAdvisorMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public Boolean isUsersReview(Integer reviewID) {
        Integer reviewUsersID = recensioneRepository.getReviewsUsersInfoID(reviewID);
        return reviewID == ((AuthUserPrincipal) this.getPrincipal()).getId();
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    @Override
    public void setFilterObject(Object obj) {
        this.filterObject = obj;
    }

    @Override
    public void setReturnObject(Object obj) {
        this.returnObject = obj;
    }
}
