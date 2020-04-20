package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.dto.RecensioneDTO;
import com.student.book_advisor.entities.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
@CrossOrigin(origins = "http://localhost:4200")
public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    @Query("SELECT new com.student.book_advisor.dto.RecensioneDTO(r.id, r.testo, r.rating, r.timestamp, r.utente.id, r.utente.username, r.libro.id, r.libro.titolo, r.libro.autori) FROM Recensione r JOIN Libro l ON r.libro.id = l.id WHERE l.id = :bookId ORDER BY r.timestamp DESC ")
    public List<RecensioneDTO> findAllByBook(@Param("bookId")Long id);

    @Query("SELECT new com.student.book_advisor.dto.RecensioneDTO(r.id, r.testo, r.rating, r.timestamp, r.utente.id, r.utente.username, r.libro.id, r.libro.titolo, r.libro.autori) FROM Recensione r JOIN Utente u ON r.utente.id = u.id WHERE u.id = :userId ORDER BY r.timestamp DESC ")
    public List<RecensioneDTO> findAllByUser(@Param("userId")Long id);

    @Query("SELECT AVG(r.rating) FROM Recensione r WHERE r.libro.id = :bookId")
    public Double getAverageRatingOfBook(@Param("bookId")Long id);


}
