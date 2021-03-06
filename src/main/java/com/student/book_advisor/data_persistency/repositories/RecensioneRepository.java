package com.student.book_advisor.data_persistency.repositories;

import com.student.book_advisor.data_persistency.model.dto.RecensioneDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.OverallRatingsForBook;
import com.student.book_advisor.data_persistency.model.entities.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecensioneRepository extends JpaRepository<Recensione, Integer> {

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.RecensioneDTO(r.id, r.testo, r.rating, r.timestamp, r.usersInfo.id, r.usersInfo.username, r.libro.id, r.libro.titolo, r.originalityRating, r.writingQualityRating, r.pageTurnerRating, r.containsSpoiler, COUNT(ur)) FROM Recensione r LEFT JOIN UsefulReview ur ON (ur.review.id = r.id) WHERE r.libro.id = :bookId GROUP BY r.id, r.testo, r.rating, r.timestamp, r.usersInfo.id, r.usersInfo.username, r.libro.id, r.libro.titolo, r.originalityRating, r.writingQualityRating, r.pageTurnerRating, r.containsSpoiler ORDER BY r.timestamp DESC ")
    public List<RecensioneDTO> findAllByBook(@Param("bookId")Integer id);

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.RecensioneDTO(r.id, r.testo, r.rating, r.timestamp, r.usersInfo.id, r.usersInfo.username, r.libro.id, r.libro.titolo, r.originalityRating, r.writingQualityRating, r.pageTurnerRating, r.containsSpoiler, COUNT(ur)) FROM Recensione r LEFT JOIN UsefulReview ur ON (ur.review.id = r.id) WHERE r.usersInfo.id = :userId GROUP BY r.id, r.testo, r.rating, r.timestamp, r.usersInfo.id, r.usersInfo.username, r.libro.id, r.libro.titolo, r.originalityRating, r.writingQualityRating, r.pageTurnerRating, r.containsSpoiler ORDER BY r.timestamp DESC ")
    public List<RecensioneDTO> findAllByUser(@Param("userId")Integer id);

    @Query("SELECT new com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.OverallRatingsForBook(AVG(r.rating), AVG(r.originalityRating), AVG(r.pageTurnerRating), AVG(r.writingQualityRating)) FROM Recensione r WHERE r.libro.id = :bookID")
    public OverallRatingsForBook getAverageRatingsOfBook(@Param("bookID")Integer bookID);

}
