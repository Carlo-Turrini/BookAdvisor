package com.student.book_advisor.services;

import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.*;
import com.student.book_advisor.services.storage.Constants;
import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.MyBooksDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.MyBooksReadDTO;
import com.student.book_advisor.data_persistency.model.entities.BookRanking;
import com.student.book_advisor.data_persistency.model.entities.MyBooks;
import com.student.book_advisor.services.storage.StorageService;
import com.student.book_advisor.enums.BookShelf;
import com.student.book_advisor.services.storage.FileUploadDir;
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
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private BookRankingService bookRankingService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean deleteFromShelf(Integer userID, Integer myBookID) {
        MyBooks myBook = myBooksRepository.getByBookIDAndUserID(myBookID, userID);
        Boolean modifiedRank = false;
        if(myBook != null) {
            if(myBook.getShelfType().compareTo(BookShelf.read)==0) {
                BookRanking bookRank = bookRankingRepository.getBookRankingByMyBooksID(myBook.getId());
                if(bookRank != null) {
                    bookRankingService.removeBookFromBookRank(userID, bookRank.getId());
                    modifiedRank = true;
                }
            }
            myBooksRepository.delete(myBook);
        }
        return modifiedRank;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String addToShelf(Integer userID, Integer bookID, BookShelf shelf) {
        MyBooks myBook = myBooksRepository.getByBookIDAndUserID(bookID, userID);
        if(myBook == null) {
            MyBooks newMyBook = new MyBooks();
            newMyBook.setShelfType(shelf);
            UsersInfo usersInfo = usersInfoRepository.findById(userID).orElse(null);
            if(usersInfo != null) {
                newMyBook.setUsersInfo(usersInfo);
                Libro book = libroRepository.findById(bookID).orElse(null);
                if(book != null) {
                    newMyBook.setBook(book);
                    myBooksRepository.save(newMyBook);
                }
                else throw new ApplicationException("Book doesn't exist!");
            }
            else throw new ApplicationException("User doesn't exist!");
            return newMyBook.getShelfType().toString();
        }
        else throw new ApplicationException("Book already part of MyBooks");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean updateShelf(Integer userID, Integer myBookID, BookShelf shelf) {
        Boolean modifiedRank = false;
        MyBooks myBook = myBooksRepository.getByBookIDAndUserID(myBookID, userID);
        if(myBook != null) {
            Boolean isOldShelfRead = myBook.getShelfType().compareTo(BookShelf.read)==0;
            myBook.setShelfType(shelf);
            myBooksRepository.save(myBook);
            if(isOldShelfRead) {
                System.out.println("OldShelf was READ");
                System.out.println(myBook.getShelfType().toString());
                BookRanking bookRank = bookRankingRepository.getBookRankingByMyBooksID(myBook.getId());
                if(bookRank != null && myBook.getShelfType().compareTo(BookShelf.read)!=0) {
                    modifiedRank = true;
                    bookRankingService.removeBookFromBookRank(userID, bookRank.getId());
                }
            }
            return modifiedRank;
        }
        else throw new ApplicationException("Book isn't part of user's mybooks");

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<MyBooksDTO> findAllMyBooks(Integer userID) {
        List<MyBooksDTO> myBooks =  myBooksRepository.getMyBooksByUserID(userID);
        for(MyBooksDTO book: myBooks) {
            book.setGenres(genreRepository.findGenresOfBook(book.getBookID()));
            book.setAuthors(authorRepository.findAuthorsOfBook(book.getBookID()));
            String bookCoverPath = libroRepository.findBookCoverPath(book.getBookID());
            if(bookCoverPath.equals(Constants.DEF_BOOK_COVER)) {
                book.setCoverImage(bookCoverPath);
            }
            else {
                book.setCoverImage(storageService.serve(bookCoverPath, FileUploadDir.coverImage));
            }
        }
        return myBooks;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<MyBooksReadDTO> findAllMyBooksRead(Integer userID) {
        return myBooksRepository.getAllMyBooksReadButNotInRank(userID);
    }
}
