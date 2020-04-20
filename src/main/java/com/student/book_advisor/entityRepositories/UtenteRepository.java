package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.dto.UtenteCardDTO;
import com.student.book_advisor.dto.UtenteDTO;
import com.student.book_advisor.entities.Utente;
import com.student.book_advisor.enums.Credenziali;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.UUID;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    @Query("SELECT new com.student.book_advisor.dto.UtenteDTO(u.id, u.nome, u.cognome, u.username, u.password, u.email, u.credenziali, u.descrizione) FROM Utente u WHERE u.username = :username")
    public UtenteDTO findByUsername(@Param("username")String username);

    @Query("SELECT new com.student.book_advisor.dto.UtenteDTO(u.id, u.nome, u.cognome, u.username, u.password, u.email, u.credenziali, u.descrizione) FROM Utente u WHERE u.id = :id")
    public UtenteDTO findUserById(@Param("id")Long id);

    @Query("SELECT new com.student.book_advisor.dto.UtenteCardDTO(u.id, u.username, u.nome, u.cognome) FROM Utente u")
    public List<UtenteCardDTO> findAllUsers();

    @Query("SELECT u.profilePhotoPath FROM Utente u WHERE u.id = :userId")
    public String getUserProfilePhotoPath(@Param("userId")Long id);

    @Query("SELECT u.authToken FROM Utente u WHERE u.username = :username AND u.password = :password")
    public String getUserAuthToken(@Param("username")String username, @Param("password")String password);

    @Query("SELECT u FROM Utente u WHERE u.username=:username AND u.password=:password")
    public Utente findByUsernameAndPassword(@Param("username")String username, @Param("password") String password);

    @Query("SELECT u.credenziali FROM Utente u WHERE u.authToken = :authToken")
    public Credenziali getUsersCredentials(@Param("authToken")String authToken);

    @Query("SELECT u.id FROM Utente u WHERE u.authToken = :authToken")
    public Long findIdByAuthToken(@Param("authToken")String authToken);

    @Query("SELECT count(u) FROM Utente u WHERE u.username = :username")
    public Integer countAllByUsername(@Param("username")String username);

    @Query("SELECT count(u) FROM Utente u WHERE u.email = :email")
    public Integer countAllByEmail(@Param("email")String email);

    public Utente findByAuthToken(String authToken);

}
