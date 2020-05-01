package com.student.book_advisor.services;

import org.springframework.stereotype.Service;

@Service
public class FolderService {
    private static String rootDir = "C:\\Users\\carlo\\BookAdvisorMedia\\";
    public static String cover_dir = "coverImages\\";
    public static String profile_dir = "profileImages\\";
    public static String author_dir = "authorImages\\";

    public static void init() {
        cover_dir = rootDir + cover_dir;
        profile_dir = rootDir + profile_dir;
        author_dir = rootDir + author_dir;
    }
}
