package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.entities.UsefulReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@Repository
@CrossOrigin(origins = "http://localhost:4200")
public interface UsefulReviewRepository extends JpaRepository<UsefulReview,Long> {

    @Query("SELECT COUNT(ur) FROM UsefulReview ur WHERE ur.review.id = :reviewID AND ur.useful = true")
    public Integer countUsefulReview(@Param("reviewID")Long reviewID);

    @Query("SELECT COUNT(ur) FROM UsefulReview ur WHERE ur.review.id = :reviewID AND ur.useful = false")
    public Integer contUnusefulReview(@Param("reviewID")Long reviewID);
}
