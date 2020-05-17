package com.student.book_advisor.services;

import com.student.book_advisor.constants.Constants;
import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.dto.BookRankingDTO;
import com.student.book_advisor.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.entities.*;
import com.student.book_advisor.entityRepositories.*;
import com.student.book_advisor.enums.BookShelf;
import com.student.book_advisor.enums.FileUploadDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookRankingServiceImpl implements BookRankingService{

    @Autowired
    private BookRankingRepository bookRankingRepository;
    @Autowired
    private UsersInfoRepository usersInfoRepository;
    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private MyBooksRepository myBooksRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private StorageService storageService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<BookRankingDTO> findUsersBookRank(Integer userID) {
        return bookRankingRepository.findBookRankingByUser(userID);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<BookRankingDTO> addBookToBookRank(Integer userID, Integer myBookID, Integer bookRank) {
        UsersInfo user = usersInfoRepository.getOne(userID);
        if(user != null) {
            List<BookRanking> bookRankingList = bookRankingRepository.findAllByUserID(userID);
            List<Integer> myBookIDsInRanking = bookRankingRepository.findAllMyBookIDsInRank(userID);
            Integer numOfBooksInRank = bookRankingList.size();
            MyBooks myBook = myBooksRepository.getByIdAndUserId(myBookID, userID);
            if (myBook != null && !myBookIDsInRanking.contains(myBookID) && myBook.getShelfType().compareTo(BookShelf.read)==0) {
                Libro bookToAdd = libroRepository.getByMyBooksID(myBookID);
                if(bookToAdd != null) {
                    if (numOfBooksInRank == 10) {
                        bookRankingRepository.delete(bookRankingList.get(9));
                    }
                    if (bookRank > numOfBooksInRank && numOfBooksInRank == 10) {
                        bookRank = 10;
                    } else if (bookRank > numOfBooksInRank + 1 && numOfBooksInRank < 10) {
                        bookRank = numOfBooksInRank + 1;
                    }
                    Integer maxIndex = numOfBooksInRank == 10 ? 8 : numOfBooksInRank - 1;
                    for (int i = maxIndex; i >= bookRank - 1; i--) {
                        BookRanking br = bookRankingList.get(i);
                        br.setBookRank(i + 1);
                        bookRankingRepository.save(br);
                    }
                    BookRanking newBookInRank = new BookRanking();
                    newBookInRank.setBookRank(bookRank);
                    newBookInRank.setMyBooks(myBook);
                    bookRankingRepository.save(newBookInRank);
                }
            }
            List<BookRankingDTO> bookRankingDTOList = bookRankingRepository.findBookRankingByUser(userID);
            for(BookRankingDTO bookRankingDTO : bookRankingDTOList) {
                List<AuthorOfBook> authors = authorRepository.findAuthorsOfBook(bookRankingDTO.getBookID());
                bookRankingDTO.setBookAuthors(authors);
                String bookCoverPhotoPath = libroRepository.findBookCoverPath(bookRankingDTO.getBookID());
                if(bookCoverPhotoPath.equals(Constants.DEF_BOOK_COVER)) {
                    bookRankingDTO.setBookCoverPhoto(bookCoverPhotoPath);
                }
                else {
                    bookRankingDTO.setBookCoverPhoto(storageService.serve(bookCoverPhotoPath, FileUploadDir.coverImage));
                }
            }
            return bookRankingDTOList;
        }
        else throw new ApplicationException("User doesn't exist");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<BookRankingDTO> removeBookFromBookRank(Integer userID, Integer bookRankID) {
        UsersInfo user = usersInfoRepository.getOne(userID);
        if(user != null) {
            List<BookRanking> bookRankingList = bookRankingRepository.findAllByUserID(userID);
            BookRanking bookRankingToDelete = bookRankingRepository.getOne(bookRankID);
            if(bookRankingRepository.getUsersIDFromBookRanking(bookRankID) == userID) {
                Integer bookRank = bookRankingToDelete.getBookRank();
                Integer bookRankListSize = bookRankingList.size();
                bookRankingRepository.delete(bookRankingToDelete);
                if(bookRank < bookRankListSize) {
                    for(int i=bookRank; i<bookRankListSize; i++) {
                        BookRanking br = bookRankingList.get(i);
                        br.setBookRank(i-1);
                        bookRankingRepository.save(br);
                    }
                }
                List<BookRankingDTO> bookRankingDTOList = bookRankingRepository.findBookRankingByUser(userID);
                for(BookRankingDTO bookRankingDTO : bookRankingDTOList) {
                    List<AuthorOfBook> authors = authorRepository.findAuthorsOfBook(bookRankingDTO.getBookID());
                    bookRankingDTO.setBookAuthors(authors);
                    String bookCoverPhotoPath = libroRepository.findBookCoverPath(bookRankingDTO.getBookID());
                    if(bookCoverPhotoPath.equals(Constants.DEF_BOOK_COVER)) {
                        bookRankingDTO.setBookCoverPhoto(bookCoverPhotoPath);
                    }
                    else {
                        bookRankingDTO.setBookCoverPhoto(storageService.serve(bookCoverPhotoPath, FileUploadDir.coverImage));
                    }
                }
                return bookRankingDTOList;
            }
            else throw new ApplicationException("This book rank doesn't beInteger to user");
        }
        else throw new ApplicationException("This user doesn't exist");
    }
}
