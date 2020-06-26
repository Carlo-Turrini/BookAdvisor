package com.student.book_advisor.services;

import com.student.book_advisor.constants.Constants;
import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.dto.MyBooksDTO;
import com.student.book_advisor.dto.auxiliaryDTOs.MyBooksReadDTO;
import com.student.book_advisor.entities.BookRanking;
import com.student.book_advisor.entities.MyBooks;
import com.student.book_advisor.entityRepositories.*;
import com.student.book_advisor.enums.BookShelf;
import com.student.book_advisor.enums.FileUploadDir;
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
            newMyBook.setUsersInfo(usersInfoRepository.getOne(userID));
            newMyBook.setBook(libroRepository.getOne(bookID));
            myBooksRepository.save(newMyBook);
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
                BookRanking bookRank = bookRankingRepository.getBookRankingByMyBooksID(myBook.getId());
                if(bookRank != null && myBook.getShelfType().compareTo(BookShelf.read)!=0) {
                    //TROVA IL MODO DI AGGIORNARE IL BOOKRANK QUANDO MODIFICHI LA SHELF -> ad ora la funzione arriva fino al comando di delete
                    // ma poi non lo esegue non capisco perché!
                    //Togliendo il cascade e l'orphan removal sia qua che nel DB funziona
                    //bisogna updatare i meccanismi di delete per l'utente e i libri di modo che facciano l'update dei ranking
                    modifiedRank = true;
                    bookRankingService.removeBookFromBookRank(userID, bookRank.getId());
                }
            }

            /*if(myBook.getShelfType().compareTo(BookShelf.read)==0) {
                BookRanking bookRank = bookRankingRepository.getBookRankingByMyBooks(myBook);
                if(bookRank != null && myBook.getShelfType().compareTo(shelf)!=0) {
                    bookRankingRepository.delete(bookRank);
                }
            }*/
            //myBook.setShelfType(shelf);
            //myBooksRepository.save(myBook);
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
