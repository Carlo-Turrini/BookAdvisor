package com.student.book_advisor.services;

import com.student.book_advisor.data_persistency.model.dto.RecensioneDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.RecensioneFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Recensione;

import java.util.List;

public interface RecensioneService {

    public List<RecensioneDTO> getAllReviewsByBook(Integer id);

    public List<RecensioneDTO> getAllReveiewsByUser(Integer id);

    public Recensione addNewReview(RecensioneFormDTO recensioneForm, Integer bookID);

    public void deleteReview(Integer reviewId);

    public Recensione getReview(Integer reviewId);

    public void addUsefulReview(Integer reviewID, Integer userID);

    public void removeUsefulReview(Integer reviewID, Integer userID);
}
