package com.student.book_advisor.services;

import com.student.book_advisor.customExceptions.StorageException;
import com.student.book_advisor.enums.FileUploadDir;
import com.student.book_advisor.enums.SupportedFileExtensions;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
@CrossOrigin(origins = "http://localhost:4200")
public class StorageServiceImpl implements StorageService {
    @Autowired
    private FolderService folderService;

    public StorageServiceImpl() {
        folderService.init();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String store(MultipartFile file, FileUploadDir tipologia, String filename) {
        try {
            System.out.println("Entro nel metodo");
            String uploadedFilename = StringUtils.cleanPath(file.getOriginalFilename());
            System.out.println("Arrivo qua");
            if (!file.isEmpty()) {
                if (uploadedFilename.contains("..")) {
                    // This is a security check
                    throw new StorageException(
                            "Non posso salvare un file con un path relativo al di fuori di questa cartella "
                                    + uploadedFilename);
                }
                String dir = null;
                String nomeFile = null;
                String oldFile = null;
                String oldExt = null;
                String fileExt = StringUtils.getFilenameExtension(uploadedFilename).toLowerCase();
                System.out.println("Entro verifico fileExt");
                if(EnumUtils.isValidEnum(SupportedFileExtensions.class, fileExt)) {
                    if(tipologia.equals(FileUploadDir.coverImage)) {
                        dir = FolderService.cover_dir;
                    }
                    else if(tipologia.equals(FileUploadDir.profileImage)) {
                        dir = FolderService.profile_dir;
                    }
                }
                else throw new StorageException("Estensione del file non valida" + uploadedFilename);
                System.out.println("Creo dir se non esiste");
                if(! new File(dir).exists())
                {
                    new File(dir).mkdir();
                }
                System.out.println("Genero nome file");
                if(filename == null ) {
                    UUID IdFile = UUID.randomUUID();
                    nomeFile = "/" + IdFile + "." +  fileExt;
                }
                else {
                    oldExt = StringUtils.getFilenameExtension(filename);
                    oldFile = StringUtils.stripFilenameExtension(filename);
                    nomeFile = oldFile + "." + fileExt;
                }
                String filePath = dir + nomeFile;
                File dest = new File(filePath);
                file.transferTo(dest);
                if(filename != null && !oldExt.equals(fileExt)) {
                    Path oldFilePath = Paths.get(dir + oldFile + "." + oldExt);
                    Files.deleteIfExists(oldFilePath);
                }
                return nomeFile;
            }
            else throw new StorageException("File vuoto " + filename);
        }

        catch(Exception e) {
            throw new StorageException("Fallimento upload: " + e.getMessage());
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String serve(String imagePath, FileUploadDir tipologia) {
        try {
            String fullPath;
            if(tipologia.equals(FileUploadDir.profileImage)) {
                fullPath = FolderService.profile_dir + imagePath;
            }
            else {
                fullPath = FolderService.cover_dir + imagePath;
            }
            String encodeBase64 = null;
            String extension = StringUtils.getFilenameExtension(imagePath);
            File imageFile = new File(fullPath);
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            byte[] imageBytes = new byte[(int) imageFile.length()];
            fileInputStream.read(imageBytes);
            encodeBase64 = Base64.getEncoder().encodeToString(imageBytes);
            String serveImage = "data:image/"+extension+";base64,"+encodeBase64;
            fileInputStream.close();
            return serveImage;
        }
        catch(Exception e) {
            throw new StorageException("Fallimento serve: " + e.getMessage());
        }

    }
}
