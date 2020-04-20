package com.student.book_advisor.services;

import com.student.book_advisor.constants.Constants;
import com.student.book_advisor.dto.RecensioneDTO;
import com.student.book_advisor.entities.Recensione;
import com.student.book_advisor.entityRepositories.LibroRepository;
import com.student.book_advisor.entityRepositories.RecensioneRepository;
import com.student.book_advisor.entityRepositories.UtenteRepository;
import com.student.book_advisor.enums.FileUploadDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Service
@CrossOrigin(origins = "http://localhost:4200")
public class RecensioneServiceImpl implements RecensioneService {

    @Autowired
    private RecensioneRepository recensioneRepo;
    @Autowired
    private StorageService storageService;
    @Autowired
    private UtenteRepository utenteRepo;
    @Autowired
    private LibroRepository libroRepo;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)

    public List<RecensioneDTO> getAllReviewsByBook(Long id) {
        List<RecensioneDTO> recensioni = recensioneRepo.findAllByBook(id);
        for(RecensioneDTO recensione: recensioni) {
            String bookCoverPath = libroRepo.findBookCoverPath(recensione.getBookId());
            String profilePhotoPath = utenteRepo.getUserProfilePhotoPath(recensione.getUserId());
            if(bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
                recensione.setCoverImage(bookCoverPath);
            }
            else {
                recensione.setCoverImage(storageService.serve(bookCoverPath, FileUploadDir.coverImage));

            }
            if(profilePhotoPath.equals(Constants.DEF_PROFILE_PIC)) {
                recensione.setProfileImage(profilePhotoPath);
            }
            else {
                recensione.setProfileImage(storageService.serve(profilePhotoPath,FileUploadDir.profileImage));

            }
        }
        return recensioni;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<RecensioneDTO> getAllReveiewsByUser(Long id) {
         List<RecensioneDTO> recensioni = recensioneRepo.findAllByUser(id);
        for(RecensioneDTO recensione: recensioni) {
            String bookCoverPath = libroRepo.findBookCoverPath(recensione.getBookId());
            String profilePhotoPath = utenteRepo.getUserProfilePhotoPath(recensione.getUserId());
            if (bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
                recensione.setCoverImage(bookCoverPath);
            } else {
                recensione.setCoverImage(storageService.serve(bookCoverPath, FileUploadDir.coverImage));

            }
            if (profilePhotoPath.equals(Constants.DEF_PROFILE_PIC)) {
                recensione.setProfileImage(profilePhotoPath);
            } else {
                recensione.setProfileImage(storageService.serve(profilePhotoPath, FileUploadDir.profileImage));

            }
        }
        return recensioni;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Recensione addNewReview(Recensione newReview) {
        return recensioneRepo.save(newReview);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteReview(Long reviewId) {
        Recensione delReview = recensioneRepo.getOne(reviewId);
        if(delReview != null) {
            recensioneRepo.delete(delReview);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Recensione getReview(Long reviewId) {
        return recensioneRepo.getOne(reviewId);
    }
}
