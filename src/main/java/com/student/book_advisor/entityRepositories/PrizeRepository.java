package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.dto.PrizeDTO;
import com.student.book_advisor.entities.Prize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrizeRepository extends JpaRepository<Prize, Long> {

    @Query("SELECT new com.student.book_advisor.dto.PrizeDTO(p.id, p.yearAwarded, p.prizeName) FROM Prize p WHERE p.book.id = :bookID")
    public List<PrizeDTO> findAllPrizesForBook(@Param("bookID")Long bookID);

}
