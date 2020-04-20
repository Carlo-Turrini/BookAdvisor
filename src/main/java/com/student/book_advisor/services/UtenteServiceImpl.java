package com.student.book_advisor.services;

import com.student.book_advisor.constants.Constants;
import com.student.book_advisor.dto.UtenteCardDTO;
import com.student.book_advisor.dto.UtenteDTO;
import com.student.book_advisor.entities.Utente;
import com.student.book_advisor.entityRepositories.UtenteRepository;
import com.student.book_advisor.enums.Credenziali;
import com.student.book_advisor.enums.FileUploadDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class UtenteServiceImpl implements UtenteService {
    @Autowired
    private UtenteRepository utenteRepo;
    @Autowired
    private StorageService storageService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<UtenteCardDTO> findAllUsers() {
        List<UtenteCardDTO> utenti = utenteRepo.findAllUsers();
        for (UtenteCardDTO utente : utenti) {
            String fotoProfiloPath = utenteRepo.getUserProfilePhotoPath(utente.getId());
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
    public UtenteDTO findById(Long id) {
        UtenteDTO user = utenteRepo.findUserById(id);
        String fotoProfiloPath = utenteRepo.getUserProfilePhotoPath(id);
        if(fotoProfiloPath.equals(Constants.DEF_PROFILE_PIC)) {
            user.setFotoProfilo(fotoProfiloPath);
        }
        else {
            user.setFotoProfilo(storageService.serve(fotoProfiloPath, FileUploadDir.profileImage));
        }
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Utente getUser(Long userId) {
        return utenteRepo.getOne(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Utente newUser(Utente newUser) {
        newUser.setAuthToken(UUID.randomUUID().toString());
        return utenteRepo.save(newUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Utente updateUser(Utente updatedUser) {
        return utenteRepo.save(updatedUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Long id) {
        Utente user = utenteRepo.getOne(id);
        if(user != null) {
            user.setDelToken(UUID.randomUUID().toString());
            utenteRepo.save(user);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Utente findUserToLogin(String username, String password) {
        return utenteRepo.findByUsernameAndPassword(username, password);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Credenziali findUsersCredentials(String authToken) {
        return utenteRepo.getUsersCredentials(authToken);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Long findIdByAuthToken(String authToken) {
        return utenteRepo.findIdByAuthToken(authToken);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isUsernameUnique(String username) {
        Integer count = utenteRepo.countAllByUsername(username);
        System.out.println(count.toString());
        if(count == null || count == 0) {
            return true;
        }
        else return false;

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isEmailUnique(String email) {
        Integer count = utenteRepo.countAllByEmail(email);
        if(count == null || count == 0) {
            return true;
        }
        else return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Utente findUserByAuthToken(String authToken) {
        return utenteRepo.findByAuthToken(authToken);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String updateUsersProfilePhoto(MultipartFile profilePhoto, Utente user) {
        String profilePath = null;
        if(!user.getProfilePhotoPath().equals(Constants.DEF_PROFILE_PIC)) {
            profilePath = user.getProfilePhotoPath();
        }
        String filePath = storageService.store(profilePhoto, FileUploadDir.profileImage, profilePath);
        user.setProfilePhotoPath(filePath);
        utenteRepo.save(user);
        String src = "{ \"img\":\""+storageService.serve(filePath, FileUploadDir.profileImage) + "\"}";
        return src;
    }
}
