package com.student.book_advisor.services;

import com.student.book_advisor.data_persistency.repositories.*;
import com.student.book_advisor.services.storage.Constants;
import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.RecensioneDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.RecensioneFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.Recensione;
import com.student.book_advisor.data_persistency.model.entities.UsefulReview;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.services.storage.FileUploadDir;
import com.student.book_advisor.security.AuthUserPrincipal;
import com.student.book_advisor.services.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private UsersInfoRepository usersInfoRepository;
    @Autowired
    private LibroRepository libroRepo;
    @Autowired
    private UsefulReviewRepository usefulReviewRepository;
    @Autowired
    private AuthorRepository authorRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<RecensioneDTO> getAllReviewsByBook(Integer id) {
        List<RecensioneDTO> recensioni = recensioneRepo.findAllByBook(id);
        for(RecensioneDTO recensione: recensioni) {
            String bookCoverPath = libroRepo.findBookCoverPath(recensione.getBookId());
            String profilePhotoPath = usersInfoRepository.getUserProfilePhotoPath(recensione.getUserId());
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
            recensione.setAutori(authorRepository.findAuthorsOfBook(recensione.getBookId()));
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
                Integer loggedUserID = ((AuthUserPrincipal) auth.getPrincipal()).getId();
                recensione.setReviewUsefulForLoggedUser(usefulReviewRepository.findByUserIDAndReviewID(loggedUserID, recensione.getId())!=null);
            }
        }
        return recensioni;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<RecensioneDTO> getAllReveiewsByUser(Integer id) {
         List<RecensioneDTO> recensioni = recensioneRepo.findAllByUser(id);
        for(RecensioneDTO recensione: recensioni) {
            String bookCoverPath = libroRepo.findBookCoverPath(recensione.getBookId());
            String profilePhotoPath = usersInfoRepository.getUserProfilePhotoPath(recensione.getUserId());
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
            recensione.setAutori(authorRepository.findAuthorsOfBook(recensione.getBookId()));
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
                Integer loggedUserID = ((AuthUserPrincipal) auth.getPrincipal()).getId();
                recensione.setReviewUsefulForLoggedUser(usefulReviewRepository.findByUserIDAndReviewID(loggedUserID, recensione.getId())!=null);
            }
        }
        return recensioni;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Recensione addNewReview(RecensioneFormDTO reviewForm, Integer bookID) {
        Recensione newReview = new Recensione();
        Libro bookToReview = libroRepo.findById(bookID).orElse(null);
        if (bookToReview != null) {
            newReview.setLibro(bookToReview);
            newReview.setUsersInfo(((AuthUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsersInfo());
            newReview.setRating(reviewForm.getRating());
            newReview.setPageTurnerRating(reviewForm.getPageTurnerRating());
            newReview.setWritingQualityRating(reviewForm.getWritingQualityRating());
            newReview.setOriginalityRating(reviewForm.getOriginalityRating());
            newReview.setContainsSpoiler(reviewForm.getContainsSpoilers());
            newReview.setTesto(reviewForm.getTesto());
            return recensioneRepo.save(newReview);
        } else throw new ApplicationException("Libro inesistente!");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteReview(Integer reviewId) {
        Recensione delReview = recensioneRepo.findById(reviewId).orElse(null);
        if(delReview != null) {
            recensioneRepo.delete(delReview);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Recensione getReview(Integer reviewId) {
        return recensioneRepo.getOne(reviewId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUsefulReview(Integer reviewID, Integer userID) {
        UsersInfo user = usersInfoRepository.findById(userID).orElse(null);
        if(user != null) {
            Recensione review = recensioneRepo.findById(reviewID).orElse(null);
            if(review != null) {
                UsefulReview ur = usefulReviewRepository.findByUserIDAndReviewID(userID, reviewID);
                if(ur == null) {
                    UsefulReview newUR = new UsefulReview();
                    newUR.setReview(review);
                    newUR.setUsersInfo(user);
                    usefulReviewRepository.save(newUR);
                }
                else throw new ApplicationException("Mi piace già messo");
            }
            else throw new ApplicationException("Recensione non esistente");
        }
        else throw new ApplicationException("User non esistente");

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeUsefulReview(Integer reviewID, Integer userID) {
        UsersInfo user = usersInfoRepository.findById(userID).orElse(null);
        if(user != null) {
            Recensione review = recensioneRepo.findById(reviewID).orElse(null);
            if(review != null) {
                UsefulReview ur = usefulReviewRepository.findByUserIDAndReviewID(userID, reviewID);
                if(ur != null) {
                    usefulReviewRepository.delete(ur);
                }
            }
        }
    }
}
