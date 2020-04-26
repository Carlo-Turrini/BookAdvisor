package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.dto.UsersInfoDTO;
import com.student.book_advisor.dto.UtenteCardDTO;
import com.student.book_advisor.dto.UtenteDTO;
import com.student.book_advisor.entities.Authorities;
import com.student.book_advisor.entities.UsersInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
@CrossOrigin(origins = "http://localhost:4200")
public interface UsersInfoRepository extends JpaRepository<UsersInfo, Long> {
    @Query("SELECT new com.student.book_advisor.dto.UsersInfoDTO(ui.id, ui.name, ui.surname, ui.username, ui.email, ui.description) FROM UsersInfo ui WHERE ui.username = :username")
    public UsersInfoDTO findUserDTOByUsername(@Param("username")String username);

    @Query("SELECT new com.student.book_advisor.dto.UsersInfoDTO(ui.id, ui.name, ui.surname, ui.username, ui.email, ui.description) FROM UsersInfo ui WHERE ui.id = :id")
    public UsersInfoDTO findUserById(@Param("id")Long id);

    @Query("SELECT new com.student.book_advisor.dto.UtenteCardDTO(ui.id, ui.username, ui.name, ui.surname) FROM UsersInfo ui")
    public List<UtenteCardDTO> findAllUsers();

    @Query("SELECT ui.profilePhotoPath FROM UsersInfo ui WHERE ui.id = :userId")
    public String getUserProfilePhotoPath(@Param("userId")Long id);

    @Query("SELECT count(ui) FROM UsersInfo ui WHERE ui.username = :username")
    public Integer countAllByUsername(@Param("username")String username);

    @Query("SELECT count(ui) FROM UsersInfo ui WHERE ui.email = :email")
    public Integer countAllByEmail(@Param("email")String email);

    public UsersInfo findByUsername(String username);
}
