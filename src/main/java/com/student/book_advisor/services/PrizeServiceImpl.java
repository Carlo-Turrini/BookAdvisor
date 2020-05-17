package com.student.book_advisor.services;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.dto.PrizeDTO;
import com.student.book_advisor.dto.formDTOS.PrizeFormDTO;
import com.student.book_advisor.entities.Libro;
import com.student.book_advisor.entities.Prize;
import com.student.book_advisor.entityRepositories.LibroRepository;
import com.student.book_advisor.entityRepositories.PrizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrizeServiceImpl implements PrizeService {

    @Autowired
    private PrizeRepository prizeRepository;
    @Autowired
    private LibroRepository libroRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deletePrize(Integer prizeID, Integer bookID) {
        Prize prizeToDelete = prizeRepository.findByIdAndBookID(prizeID, bookID);
        if(prizeToDelete != null) {
            prizeRepository.delete(prizeToDelete);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addPrize(PrizeFormDTO prizeForm, Integer bookID) {
        Libro book = libroRepository.getOne(bookID);
        if(book != null) {
            Prize prize = new Prize();
            prize.setPrizeName(prizeForm.getPrizeName());
            prize.setYearAwarded(prizeForm.getYearAwarded());
            prize.setBook(book);
            prizeRepository.save(prize);
        }
        else throw new ApplicationException("This book doesn't exist!");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<PrizeDTO> getAllPrizesOfBook(Integer bookID) {
        return prizeRepository.findAllPrizesForBook(bookID);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean isPrizeAlreadyAssignedToBook(String prizeName, Integer bookID) {
        return prizeRepository.findByBookIDAndPrizeName(bookID, prizeName) != null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PrizeDTO getPrizeOfBookFromName(Integer bookID, String prizeName) {
        return prizeRepository.findDTOByBookIDAndPrizeName(bookID, prizeName);
    }
}
