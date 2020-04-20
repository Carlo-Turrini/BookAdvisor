package com.student.book_advisor.services;

import com.student.book_advisor.enums.FileUploadDir;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    public String store(MultipartFile file, FileUploadDir tipologia, String filename);

    public String serve(String imagePath, FileUploadDir tipologia);
}
