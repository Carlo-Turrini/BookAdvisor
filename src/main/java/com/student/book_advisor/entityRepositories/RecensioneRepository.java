package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.dto.RecensioneDTO;
import com.student.book_advisor.dto.auxiliaryDTOs.OverallRatingsForBook;
import com.student.book_advisor.entities.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Repository
public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    @Query("SELECT new com.student.book_advisor.dto.RecensioneDTO(r.id, r.testo, r.rating, r.timestamp, r.usersInfo.id, r.usersInfo.username, r.libro.id, r.libro.titolo, r.originalityRating, r.writingQualityRating, r.pageTurnerRating, r.containsSpoiler) FROM Recensione r WHERE r.libro.id = :bookId ORDER BY r.timestamp DESC ")
    public List<RecensioneDTO> findAllByBook(@Param("bookId")Long id);

    @Query("SELECT new com.student.book_advisor.dto.RecensioneDTO(r.id, r.testo, r.rating, r.timestamp, r.usersInfo.id, r.usersInfo.username, r.libro.id, r.libro.titolo, r.originalityRating, r.writingQualityRating, r.pageTurnerRating, r.containsSpoiler) FROM Recensione r WHERE r.usersInfo.id = :userId ORDER BY r.timestamp DESC ")
    public List<RecensioneDTO> findAllByUser(@Param("userId")Long id);

    @Query("SELECT AVG(r.rating) FROM Recensione r WHERE r.libro.id = :bookId")
    public Double getAverageRatingOfBook(@Param("bookId")Long id);

    @Query("SELECT new com.student.book_advisor.dto.auxiliaryDTOs.OverallRatingsForBook(AVG(r.rating), AVG(r.originalityRating), AVG(r.pageTurnerRating), AVG(r.writingQualityRating)) FROM Recensione r WHERE r.libro.id = :bookID")
    public OverallRatingsForBook getAverageRatingsOfBook(@Param("bookID")Long bookID);

    @Query("SELECT r.usersInfo.id FROM Recensione r WHERE r.id = :reviewID")
    public Long getReviewsUsersInfoID(@Param("reviewID")Long reviewID);
}
