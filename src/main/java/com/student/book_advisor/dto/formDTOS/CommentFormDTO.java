package com.student.book_advisor.dto.formDTOS;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CommentFormDTO {
    @NotBlank
    @Size(min = 1, max = 1024)
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
