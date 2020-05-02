package com.student.book_advisor.services;

import com.student.book_advisor.dto.PrizeDTO;
import com.student.book_advisor.dto.formDTOS.PrizeFormDTO;

import java.util.List;

public interface PrizeService {

    public void deletePrize(Long prizeID, Long bookID);

    public void addPrize(PrizeFormDTO prizeForm, Long bookID);

    public List<PrizeDTO> getAllPrizesOfBook(Long bookID);

    public Boolean isPrizeAlreadyAssignedToBook(String prizeName, Long bookID);
}
