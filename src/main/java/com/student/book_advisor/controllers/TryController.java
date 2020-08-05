package com.student.book_advisor.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class TryController {
    /*@RequestMapping("/")
    public String provide() {
        return "index";
    }*/
    @RequestMapping({"/", "/new-user"})
    public String provide(HttpServletRequest request) {
        return "/index.html";
    }
}
