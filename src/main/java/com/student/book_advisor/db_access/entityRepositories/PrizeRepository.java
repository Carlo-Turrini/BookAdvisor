package com.student.book_advisor.db_access.entityRepositories;

import com.student.book_advisor.db_access.dto.PrizeDTO;
import com.student.book_advisor.db_access.entities.Prize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrizeRepository extends JpaRepository<Prize, Integer> {

    @Query("SELECT new com.student.book_advisor.db_access.dto.PrizeDTO(p.id, p.yearAwarded, p.prizeName) FROM Prize p WHERE p.book.id = :bookID")
    public List<PrizeDTO> findAllPrizesForBook(@Param("bookID")Integer bookID);

    @Query("SELECT p FROM Prize p WHERE p.id = :id AND p.book.id = :bookID")
    public Prize findByIdAndBookID(@Param("id")Integer id, @Param("bookID")Integer bookID);

    @Query("SELECT p FROM Prize p WHERE p.book.id = :bookID AND p.prizeName = :prizeName")
    public Prize findByBookIDAndPrizeName(@Param("bookID")Integer bookID, @Param("prizeName")String prizeName);

    @Query("SELECT new com.student.book_advisor.db_access.dto.PrizeDTO(p.id, p.yearAwarded, p.prizeName) FROM Prize p WHERE p.book.id = :bookID AND p.prizeName = :prizeName")
    public PrizeDTO findDTOByBookIDAndPrizeName(@Param("bookID")Integer bookID, @Param("prizeName")String prizeName);
}
