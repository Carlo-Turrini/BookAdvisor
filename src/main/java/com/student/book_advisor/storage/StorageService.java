package com.student.book_advisor.storage;

import com.student.book_advisor.storage.FileUploadDir;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    public String store(MultipartFile file, FileUploadDir tipologia, String filename);

    public String serve(String imagePath, FileUploadDir tipologia);
}
