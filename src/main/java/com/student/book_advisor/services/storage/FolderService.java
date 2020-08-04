package com.student.book_advisor.services.storage;

import org.springframework.stereotype.Service;

@Service
public class FolderService {
    private static String rootDir = "C:\\Users\\carlo\\BookAdvisorMedia\\";
    public static String cover_dir = rootDir + "coverImages\\";
    public static String profile_dir = rootDir + "profileImages\\";
    public static String author_dir = rootDir + "authorImages\\";
}
