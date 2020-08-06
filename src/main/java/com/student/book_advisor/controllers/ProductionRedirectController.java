package com.student.book_advisor.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ProductionRedirectController {

    @RequestMapping("/")
    public String redirectHome() {
        return "redirect:/ba/home";
    }
}
