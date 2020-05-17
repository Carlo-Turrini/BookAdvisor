package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.entities.UsefulReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

@Repository
@CrossOrigin(origins = "http://localhost:4200")
public interface UsefulReviewRepository extends JpaRepository<UsefulReview,Integer> {

    @Query("SELECT COUNT(ur) FROM UsefulReview ur WHERE ur.review.id = :reviewID")
    public Integer countUsefulReview(@Param("reviewID")Integer reviewID);

    @Query("SELECT ur FROM UsefulReview ur WHERE ur.usersInfo.id = :userID AND ur.review.id = :reviewID")
    public UsefulReview findByUserIDAndReviewID(@Param("userID")Integer userID, @Param("reviewID")Integer reviewID);
}
