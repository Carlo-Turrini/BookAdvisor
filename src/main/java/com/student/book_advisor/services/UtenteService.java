package com.student.book_advisor.services;

import com.student.book_advisor.data_persistency.model.dto.*;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.UtenteFormDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.UtenteUpdateFormDTO;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UtenteService {

    public List<UtenteCardDTO> findAllUsers();

    public UsersInfoDTO findById(Integer id);

    public UsersInfo getUser(Integer userId);

    public UsersInfo newUser(UtenteFormDTO userForm);

    public UsersInfo updateUser(UsersInfo updatedUser, UtenteUpdateFormDTO userForm);

    public void deleteUser(Integer id);

    public boolean isUsernameUnique(String username);

    public boolean isEmailUnique(String email);

    public String updateUsersProfilePhoto(MultipartFile profilePhoto, Integer userID);




}
