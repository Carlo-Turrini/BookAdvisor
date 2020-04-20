package com.student.book_advisor.customExceptions;

public class ApplicationException extends RuntimeException{
    private static final long serialVersionUID = 643330230602231759L;

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}

