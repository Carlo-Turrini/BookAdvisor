package com.student.book_advisor.entityRepositories;

import com.student.book_advisor.dto.CommentDTO;
import com.student.book_advisor.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT new com.student.book_advisor.dto.CommentDTO(c.id, c.usersInfo.id, c.review.id, c.text, c.createdDate) FROM Comment c WHERE c.review.id = :reviewID")
    public List<CommentDTO> findAllCommentsOfReview(@Param("reviewID")Long reviewID);
}
