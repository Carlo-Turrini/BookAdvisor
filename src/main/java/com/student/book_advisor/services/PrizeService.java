package com.student.book_advisor.services;

import com.student.book_advisor.db_access.dto.PrizeDTO;
import com.student.book_advisor.db_access.dto.formDTOS.PrizeFormDTO;

import java.util.List;

public interface PrizeService {

    public void deletePrize(Integer prizeID, Integer bookID);

    public void addPrize(PrizeFormDTO prizeForm, Integer bookID);

    public List<PrizeDTO> getAllPrizesOfBook(Integer bookID);

    public Boolean isPrizeAlreadyAssignedToBook(String prizeName, Integer bookID);

    public PrizeDTO getPrizeOfBookFromName(Integer bookID, String prizeName);
}
