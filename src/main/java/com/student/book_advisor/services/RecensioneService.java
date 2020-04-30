package com.student.book_advisor.services;

import com.student.book_advisor.dto.RecensioneDTO;
import com.student.book_advisor.entities.Recensione;

import java.util.List;

public interface RecensioneService {

    public List<RecensioneDTO> getAllReviewsByBook(Long id);

    public List<RecensioneDTO> getAllReveiewsByUser(Long id);

    public Recensione addNewReview(Recensione newReview);

    public void deleteReview(Long reviewId);

    public Recensione getReview(Long reviewId);

    public Integer addUsefulReview(Long reviewID, Long userID);

    public Integer removeUsefulReview(Long reviewID, Long userID);
}
