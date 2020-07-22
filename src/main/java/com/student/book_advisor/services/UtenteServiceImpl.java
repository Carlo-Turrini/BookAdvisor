package com.student.book_advisor.services;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.repositories.AuthoritiesRepository;
import com.student.book_advisor.data_persistency.repositories.BookRankingRepository;
import com.student.book_advisor.data_persistency.repositories.UsefulReviewRepository;
import com.student.book_advisor.data_persistency.repositories.UsersInfoRepository;
import com.student.book_advisor.services.storage.Constants;
import com.student.book_advisor.data_persistency.model.dto.*;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.UtenteFormDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.UtenteUpdateFormDTO;
import com.student.book_advisor.data_persistency.model.entities.*;
import com.student.book_advisor.services.storage.FileUploadDir;
import com.student.book_advisor.services.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UtenteServiceImpl implements UtenteService {
    @Autowired
    private StorageService storageService;
    @Autowired
    private UsersInfoRepository usersInfoRepository;
    @Autowired
    private AuthoritiesRepository authoritiesRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private BookRankingRepository bookRankingRepository;
    @Autowired
    private UsefulReviewRepository usefulReviewRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<UtenteCardDTO> findAllUsers() {
        List<UtenteCardDTO> utenti = usersInfoRepository.findAllUsers();
        for (UtenteCardDTO utente : utenti) {
            String fotoProfiloPath = usersInfoRepository.getUserProfilePhotoPath(utente.getId());
            if(fotoProfiloPath.equals(Constants.DEF_PROFILE_PIC)) {
                utente.setProfileImage(fotoProfiloPath);
            }
            else {
                utente.setProfileImage(storageService.serve(fotoProfiloPath, FileUploadDir.profileImage));
            }
        }
        return utenti;
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersInfoDTO findById(Integer id) {
        UsersInfoDTO user = usersInfoRepository.findUserById(id);
        String fotoProfiloPath = usersInfoRepository.getUserProfilePhotoPath(id);
        if(fotoProfiloPath.equals(Constants.DEF_PROFILE_PIC)) {
            user.setProfilePhoto(fotoProfiloPath);
        }
        else {
            user.setProfilePhoto(storageService.serve(fotoProfiloPath, FileUploadDir.profileImage));
        }
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersInfo getUser(Integer userId) {
        return usersInfoRepository.getOne(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersInfo newUser(UtenteFormDTO userForm) {
        UsersInfo newUser = new UsersInfo();
        newUser.setName(userForm.getNome());
        newUser.setSurname(userForm.getCognome());
        newUser.setUsername(userForm.getUsername());
        newUser.setPassword(passwordEncoder.encode(userForm.getPassword()));
        newUser.setEmail(userForm.getEmail());
        newUser.setDescription(userForm.getDescrizione());
        newUser = usersInfoRepository.save(newUser);
        //Set newUsers authority in application domain.
        Authorities authority = new Authorities();
        authority.setAuthority("USER");
        authority.setUsersInfo(newUser);
        authoritiesRepository.save(authority);
        return newUser;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersInfo updateUser(UsersInfo updatedUser, UtenteUpdateFormDTO userForm) {
        updatedUser.setName(userForm.getNome());
        updatedUser.setSurname(userForm.getCognome());
        if(userForm.getPassword() != null && !userForm.getPassword().isEmpty()) {
            updatedUser.setPassword(passwordEncoder.encode(userForm.getPassword()));
        }
        updatedUser.setEmail(userForm.getEmail());
        updatedUser.setDescription(userForm.getDescrizione());
        return usersInfoRepository.save(updatedUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Integer id) {
        UsersInfo user = usersInfoRepository.findById(id).orElse(null);
        if(user != null) {
            List<BookRanking> bookRankingList = bookRankingRepository.findAllByUserID(id);
            bookRankingRepository.deleteInBatch(bookRankingList);
            List<UsefulReview> usefulReviewList = usefulReviewRepository.findAllUsefulReviewsByUserID(id);
            usefulReviewRepository.deleteInBatch(usefulReviewList);
            String photoPath = user.getProfilePhotoPath();
            if(photoPath != Constants.DEF_PROFILE_PIC) {
                storageService.delete(photoPath, FileUploadDir.profileImage);
            }
            //user.setDelToken(UUID.randomUUID().toString());
            usersInfoRepository.delete(user);
        }
    }

    /*@Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UsersInfo findUserToLogin(String username, String password) {
        return usersInfoRepository.findByUsernameAndPassword(username, password);
    }*/

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isUsernameUnique(String username) {
        Integer count = usersInfoRepository.countAllByUsername(username);
        if(count == null || count == 0) {
            return true;
        }
        else return false;

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isEmailUnique(String email) {
        Integer count = usersInfoRepository.countAllByEmail(email);
        if(count == null || count == 0) {
            return true;
        }
        else return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String updateUsersProfilePhoto(MultipartFile profilePhoto, Integer userID) {
        UsersInfo user = usersInfoRepository.findById(userID).orElse(null);
        if(user != null) {
            String profilePath = null;
            if(!user.getProfilePhotoPath().equals(Constants.DEF_PROFILE_PIC)) {
                profilePath = user.getProfilePhotoPath();
            }
            String filePath = storageService.store(profilePhoto, FileUploadDir.profileImage, profilePath);
            user.setProfilePhotoPath(filePath);
            usersInfoRepository.save(user);
            String src = "{ \"img\":\""+storageService.serve(filePath, FileUploadDir.profileImage) + "\"}";
            return src;
        }
        else throw new ApplicationException("User not found!");

    }
}
