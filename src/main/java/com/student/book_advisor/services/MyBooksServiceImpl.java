package com.student.book_advisor.services;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.dto.MyBooksDTO;
import com.student.book_advisor.dto.auxiliaryDTOs.MyBooksReadDTO;
import com.student.book_advisor.entities.BookRanking;
import com.student.book_advisor.entities.MyBooks;
import com.student.book_advisor.entityRepositories.BookRankingRepository;
import com.student.book_advisor.entityRepositories.LibroRepository;
import com.student.book_advisor.entityRepositories.MyBooksRepository;
import com.student.book_advisor.entityRepositories.UsersInfoRepository;
import com.student.book_advisor.enums.BookShelf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MyBooksServiceImpl implements MyBooksService {
    @Autowired
    private MyBooksRepository myBooksRepository;
    @Autowired
    private UsersInfoRepository usersInfoRepository;
    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private BookRankingRepository bookRankingRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteFromShelf(Integer userID, Integer myBookID) {
        myBooksRepository.deleteMyBookByUserIDAndId(myBookID, userID);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String addToShelf(Integer userID, Integer bookID, BookShelf shelf) {
        MyBooks myBook = myBooksRepository.getByBookIDAndUserID(bookID, userID);
        if(myBook == null) {
            MyBooks newMyBook = new MyBooks();
            newMyBook.setShelfType(shelf);
            newMyBook.setUsersInfo(usersInfoRepository.getOne(userID));
            newMyBook.setBook(libroRepository.getOne(bookID));
            myBooksRepository.save(newMyBook);
            return newMyBook.getShelfType().toString();
        }
        else throw new ApplicationException("Book already part of MyBooks");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String updateShelf(Integer userID, Integer myBookID, BookShelf shelf) {
        MyBooks myBook = myBooksRepository.getByBookIDAndUserID(myBookID, userID);
        if(myBook != null) {
            if(myBook.getShelfType().compareTo(BookShelf.read)==0) {
                BookRanking bookRank = bookRankingRepository.getBookRankingByMyBooks(myBook);
                if(bookRank != null && myBook.getShelfType().compareTo(shelf)!=0) {
                    bookRankingRepository.delete(bookRank);
                }
            }
            myBook.setShelfType(shelf);
            myBooksRepository.save(myBook);
            return myBook.getShelfType().toString();
        }
        else throw new ApplicationException("Book isn't part of user's mybooks");

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<MyBooksDTO> findAllMyBooks(Integer userID) {
        return myBooksRepository.getMyBooksByUserID(userID);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<MyBooksReadDTO> findAllMyBooksRead(Integer userID) {
        return myBooksRepository.getAllMyBooksReadButNotInRank(userID);
    }
}
