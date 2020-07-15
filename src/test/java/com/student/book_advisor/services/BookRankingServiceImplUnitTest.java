package com.student.book_advisor.services;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.BookRankingDTO;
import com.student.book_advisor.data_persistency.model.entities.BookRanking;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.MyBooks;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.*;
import com.student.book_advisor.enums.BookShelf;
import com.student.book_advisor.services.storage.Constants;
import com.student.book_advisor.services.storage.FileUploadDir;
import com.student.book_advisor.services.storage.StorageService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
public class BookRankingServiceImplUnitTest {
    @TestConfiguration
    static class BookRankingServiceImplTestContextConfiguration {
        @Bean
        public BookRankingService bookRankingService() {
            return new BookRankingServiceImpl();
        }
    }

    @Autowired
    private BookRankingService bookRankingService;

    @MockBean
    private BookRankingRepository bookRankingRepository;

    @MockBean
    private UsersInfoRepository usersInfoRepository;

    @MockBean
    private LibroRepository libroRepository;

    @MockBean
    private MyBooksRepository myBooksRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private StorageService storageService;

    @Before
    public void setup() {

    }

    @Test
    public void testFindUsersBookRank() {
        List<BookRankingDTO> bookRankingDTOList = new ArrayList<>();
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Prova1");
        BookRankingDTO bookRankingDTO1 = new BookRankingDTO(2, 2, 2, "Prova2");
        bookRankingDTOList.add(bookRankingDTO);
        bookRankingDTOList.add(bookRankingDTO1);
        Mockito.when(bookRankingRepository.findBookRankingByUser(Mockito.anyInt())).thenReturn(bookRankingDTOList);
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO.getId())).thenReturn("/notDefaultImage");
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO1.getId())).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<BookRankingDTO> found = bookRankingService.findUsersBookRank(1);
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(bookRankingDTOList.size());
        assertThat(found.contains(bookRankingDTO)).isTrue();
        assertThat(found.contains(bookRankingDTO1)).isTrue();
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findBookRankingByUser(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingRepository);
        Mockito.verify(authorRepository, Mockito.times(bookRankingDTOList.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(libroRepository, Mockito.times(bookRankingDTOList.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testAddBookToBookRank_userNull() {
        Mockito.when(usersInfoRepository.getOne(Mockito.anyInt())).thenReturn(null);
        assertThatThrownBy(() -> bookRankingService.addBookToBookRank(1, 1, 1))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("User doesn't exist");
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verifyNoInteractions(bookRankingRepository);
        Mockito.verifyNoInteractions(myBooksRepository);
        Mockito.verifyNoInteractions(authorRepository);
        Mockito.verifyNoInteractions(libroRepository);
        Mockito.verifyNoInteractions(storageService);
    }

    @Test
    public void testAddBookToBookRank_userNotNull_numOfBooksInRankIs10_addFirst() {
        Integer userId = 1;
        Integer myBooksId = 11;
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.getOne(Mockito.anyInt())).thenReturn(usersInfo);
        List<BookRanking> bookRankingList = getBookRankingListFull();
        Mockito.when(bookRankingRepository.findAllByUserID(Mockito.anyInt())).thenReturn(bookRankingList);
        List<Integer> allMyBooksInRankIDs = getAllMyBooksInRankListFull();
        Mockito.when(bookRankingRepository.findAllMyBookIDsInRank(Mockito.anyInt())).thenReturn(allMyBooksInRankIDs);
        MyBooks myBooks = new MyBooks();
        myBooks.setId(myBooksId);
        myBooks.setShelfType(BookShelf.read);
        Mockito.when(myBooksRepository.getByIdAndUserId(myBooksId, userId)).thenReturn(myBooks);
        Mockito.when(libroRepository.getByMyBooksID(Mockito.anyInt())).thenReturn(new Libro());
        Mockito.when(bookRankingRepository.save(Mockito.any(BookRanking.class))).thenAnswer(i -> i.getArguments()[0]);
        //Essendo la parte che resituisce la lista identica al metodo findUsersBookRank, la testiamo in maniera fittizia
        List<BookRankingDTO> bookRankingDTOList = new ArrayList<>();
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Prova1");
        BookRankingDTO bookRankingDTO1 = new BookRankingDTO(2, 2, 2, "Prova2");
        bookRankingDTOList.add(bookRankingDTO);
        bookRankingDTOList.add(bookRankingDTO1);
        Mockito.when(bookRankingRepository.findBookRankingByUser(Mockito.anyInt())).thenReturn(bookRankingDTOList);
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO.getId())).thenReturn("/notDefaultImage");
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO1.getId())).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<BookRankingDTO> found = bookRankingService.addBookToBookRank(userId, myBooksId, 1);
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(bookRankingDTOList.size());
        assertThat(found.contains(bookRankingDTO)).isTrue();
        assertThat(found.contains(bookRankingDTO1)).isTrue();
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findAllByUserID(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findAllMyBookIDsInRank(Mockito.anyInt());
        Mockito.verify(myBooksRepository, Mockito.times(1)).getByIdAndUserId(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(myBooksRepository);
        Mockito.verify(libroRepository, Mockito.times(1)).getByMyBooksID(Mockito.any());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).delete(Mockito.any(BookRanking.class));
        Mockito.verify(bookRankingRepository, Mockito.times(10)).save(Mockito.any(BookRanking.class));
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findBookRankingByUser(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingRepository);
        Mockito.verify(authorRepository, Mockito.times(bookRankingDTOList.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(libroRepository, Mockito.times(bookRankingDTOList.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testAddBookToBookRank_userNotNull_numOfBooksInRankIs10_addLast() {
        Integer userId = 1;
        Integer myBooksId = 11;
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.getOne(Mockito.anyInt())).thenReturn(usersInfo);
        List<BookRanking> bookRankingList = getBookRankingListFull();
        Mockito.when(bookRankingRepository.findAllByUserID(Mockito.anyInt())).thenReturn(bookRankingList);
        List<Integer> allMyBooksInRankIDs = getAllMyBooksInRankListFull();
        Mockito.when(bookRankingRepository.findAllMyBookIDsInRank(Mockito.anyInt())).thenReturn(allMyBooksInRankIDs);
        MyBooks myBooks = new MyBooks();
        myBooks.setId(myBooksId);
        myBooks.setShelfType(BookShelf.read);
        Mockito.when(myBooksRepository.getByIdAndUserId(myBooksId, userId)).thenReturn(myBooks);
        Mockito.when(libroRepository.getByMyBooksID(Mockito.anyInt())).thenReturn(new Libro());
        Mockito.when(bookRankingRepository.save(Mockito.any(BookRanking.class))).thenAnswer(i -> i.getArguments()[0]);
        //Essendo la parte che resituisce la lista identica al metodo findUsersBookRank, la testiamo in maniera fittizia
        List<BookRankingDTO> bookRankingDTOList = new ArrayList<>();
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Prova1");
        BookRankingDTO bookRankingDTO1 = new BookRankingDTO(2, 2, 2, "Prova2");
        bookRankingDTOList.add(bookRankingDTO);
        bookRankingDTOList.add(bookRankingDTO1);
        Mockito.when(bookRankingRepository.findBookRankingByUser(Mockito.anyInt())).thenReturn(bookRankingDTOList);
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO.getId())).thenReturn("/notDefaultImage");
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO1.getId())).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");

        List<BookRankingDTO> found = bookRankingService.addBookToBookRank(userId, myBooksId, 10);
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(bookRankingDTOList.size());
        assertThat(found.contains(bookRankingDTO)).isTrue();
        assertThat(found.contains(bookRankingDTO1)).isTrue();
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findAllByUserID(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findAllMyBookIDsInRank(Mockito.anyInt());
        Mockito.verify(myBooksRepository, Mockito.times(1)).getByIdAndUserId(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(myBooksRepository);
        Mockito.verify(libroRepository, Mockito.times(1)).getByMyBooksID(Mockito.any());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).delete(Mockito.any(BookRanking.class));
        Mockito.verify(bookRankingRepository, Mockito.times(1)).save(Mockito.any(BookRanking.class));
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findBookRankingByUser(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingRepository);
        Mockito.verify(authorRepository, Mockito.times(bookRankingDTOList.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(libroRepository, Mockito.times(bookRankingDTOList.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testAddBookToBookRank_userNotNull_numOfBooksInRankLessThan10_addFirst() {
        Integer userId = 1;
        Integer myBooksId = 11;
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.getOne(Mockito.anyInt())).thenReturn(usersInfo);
        List<BookRanking> bookRankingList = getBookRankingListPartiallyFull();
        Mockito.when(bookRankingRepository.findAllByUserID(Mockito.anyInt())).thenReturn(bookRankingList);
        List<Integer> allMyBooksInRankIDs = getAllMyBooksInRankListPartiallyFull();
        Mockito.when(bookRankingRepository.findAllMyBookIDsInRank(Mockito.anyInt())).thenReturn(allMyBooksInRankIDs);
        MyBooks myBooks = new MyBooks();
        myBooks.setId(myBooksId);
        myBooks.setShelfType(BookShelf.read);
        Mockito.when(myBooksRepository.getByIdAndUserId(myBooksId, userId)).thenReturn(myBooks);
        Mockito.when(libroRepository.getByMyBooksID(Mockito.anyInt())).thenReturn(new Libro());
        Mockito.when(bookRankingRepository.save(Mockito.any(BookRanking.class))).thenAnswer(i -> i.getArguments()[0]);
        //Essendo la parte che resituisce la lista identica al metodo findUsersBookRank, la testiamo in maniera fittizia
        List<BookRankingDTO> bookRankingDTOList = new ArrayList<>();
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Prova1");
        BookRankingDTO bookRankingDTO1 = new BookRankingDTO(2, 2, 2, "Prova2");
        bookRankingDTOList.add(bookRankingDTO);
        bookRankingDTOList.add(bookRankingDTO1);
        Mockito.when(bookRankingRepository.findBookRankingByUser(Mockito.anyInt())).thenReturn(bookRankingDTOList);
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO.getId())).thenReturn("/notDefaultImage");
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO1.getId())).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<BookRankingDTO> found = bookRankingService.addBookToBookRank(userId, myBooksId, 1);
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(bookRankingDTOList.size());
        assertThat(found.contains(bookRankingDTO)).isTrue();
        assertThat(found.contains(bookRankingDTO1)).isTrue();
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findAllByUserID(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findAllMyBookIDsInRank(Mockito.anyInt());
        Mockito.verify(myBooksRepository, Mockito.times(1)).getByIdAndUserId(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(myBooksRepository);
        Mockito.verify(libroRepository, Mockito.times(1)).getByMyBooksID(Mockito.any());
        Mockito.verify(bookRankingRepository, Mockito.times(0)).delete(Mockito.any(BookRanking.class));
        Mockito.verify(bookRankingRepository, Mockito.times(6)).save(Mockito.any(BookRanking.class));
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findBookRankingByUser(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingRepository);
        Mockito.verify(authorRepository, Mockito.times(bookRankingDTOList.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(libroRepository, Mockito.times(bookRankingDTOList.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testAddBookToBookRank_userNotNull_numOfBooksInRankLessThan10_addLast() {
        Integer userId = 1;
        Integer myBooksId = 11;
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.getOne(Mockito.anyInt())).thenReturn(usersInfo);
        List<BookRanking> bookRankingList = getBookRankingListPartiallyFull();
        Mockito.when(bookRankingRepository.findAllByUserID(Mockito.anyInt())).thenReturn(bookRankingList);
        List<Integer> allMyBooksInRankIDs = getAllMyBooksInRankListPartiallyFull();
        Mockito.when(bookRankingRepository.findAllMyBookIDsInRank(Mockito.anyInt())).thenReturn(allMyBooksInRankIDs);
        MyBooks myBooks = new MyBooks();
        myBooks.setId(myBooksId);
        myBooks.setShelfType(BookShelf.read);
        Mockito.when(myBooksRepository.getByIdAndUserId(myBooksId, userId)).thenReturn(myBooks);
        Mockito.when(libroRepository.getByMyBooksID(Mockito.anyInt())).thenReturn(new Libro());
        Mockito.when(bookRankingRepository.save(Mockito.any(BookRanking.class))).thenAnswer(i -> i.getArguments()[0]);
        //Essendo la parte che resituisce la lista identica al metodo findUsersBookRank, la testiamo in maniera fittizia
        List<BookRankingDTO> bookRankingDTOList = new ArrayList<>();
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Prova1");
        BookRankingDTO bookRankingDTO1 = new BookRankingDTO(2, 2, 2, "Prova2");
        bookRankingDTOList.add(bookRankingDTO);
        bookRankingDTOList.add(bookRankingDTO1);
        Mockito.when(bookRankingRepository.findBookRankingByUser(Mockito.anyInt())).thenReturn(bookRankingDTOList);
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO.getId())).thenReturn("/notDefaultImage");
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO1.getId())).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<BookRankingDTO> found = bookRankingService.addBookToBookRank(userId, myBooksId, 10);
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(bookRankingDTOList.size());
        assertThat(found.contains(bookRankingDTO)).isTrue();
        assertThat(found.contains(bookRankingDTO1)).isTrue();
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findAllByUserID(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findAllMyBookIDsInRank(Mockito.anyInt());
        Mockito.verify(myBooksRepository, Mockito.times(1)).getByIdAndUserId(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(myBooksRepository);
        Mockito.verify(libroRepository, Mockito.times(1)).getByMyBooksID(Mockito.any());
        Mockito.verify(bookRankingRepository, Mockito.times(0)).delete(Mockito.any(BookRanking.class));
        Mockito.verify(bookRankingRepository, Mockito.times(1)).save(Mockito.any(BookRanking.class));
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findBookRankingByUser(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingRepository);
        Mockito.verify(authorRepository, Mockito.times(bookRankingDTOList.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(libroRepository, Mockito.times(bookRankingDTOList.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testRemoveBookFromBookRank_userNull() {
        Mockito.when(usersInfoRepository.getOne(Mockito.anyInt())).thenReturn(null);
        assertThatThrownBy(() -> bookRankingService.removeBookFromBookRank(1, 1))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("This user doesn't exist");
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verifyNoInteractions(bookRankingRepository);
        Mockito.verifyNoInteractions(myBooksRepository);
        Mockito.verifyNoInteractions(authorRepository);
        Mockito.verifyNoInteractions(libroRepository);
        Mockito.verifyNoInteractions(storageService);
    }

    @Test
    public void testRemoveBookFromBookRank_bookRankDoesNotBelongToUser() {
        Integer usersId = 3;
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.getOne(Mockito.anyInt())).thenReturn(usersInfo);
        Mockito.when(bookRankingRepository.findAllByUserID(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(bookRankingRepository.getOne(Mockito.anyInt())).thenReturn(new BookRanking());
        Mockito.when(bookRankingRepository.getUsersIDFromBookRanking(Mockito.anyInt())).thenReturn(usersId + 1);
        assertThatThrownBy(() -> bookRankingService.removeBookFromBookRank(usersId, 1))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("This book rank doesn't beInteger to user");
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findAllByUserID(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).getUsersIDFromBookRanking(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingRepository);
        Mockito.verifyNoInteractions(myBooksRepository);
        Mockito.verifyNoInteractions(authorRepository);
        Mockito.verifyNoInteractions(libroRepository);
        Mockito.verifyNoInteractions(storageService);
    }

    @Test
    public void testRemoveBookFromBookRank_removeFirst() {
        Integer usersId = 3;
        Integer bookRankId = 1;
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.getOne(Mockito.anyInt())).thenReturn(usersInfo);
        List<BookRanking> bookRankingList = getBookRankingListPartiallyFull();
        Mockito.when(bookRankingRepository.findAllByUserID(Mockito.anyInt())).thenReturn(bookRankingList);
        Mockito.when(bookRankingRepository.getOne(bookRankId)).thenReturn(bookRankingList.get(bookRankId-1));
        Mockito.when(bookRankingRepository.getUsersIDFromBookRanking(bookRankId)).thenReturn(usersId);
        Mockito.when(bookRankingRepository.save(Mockito.any(BookRanking.class))).thenAnswer(i -> i.getArguments()[0]);
        //Essendo la parte che resituisce la lista identica al metodo findUsersBookRank, la testiamo in maniera fittizia
        List<BookRankingDTO> bookRankingDTOList = new ArrayList<>();
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Prova1");
        BookRankingDTO bookRankingDTO1 = new BookRankingDTO(2, 2, 2, "Prova2");
        bookRankingDTOList.add(bookRankingDTO);
        bookRankingDTOList.add(bookRankingDTO1);
        Mockito.when(bookRankingRepository.findBookRankingByUser(Mockito.anyInt())).thenReturn(bookRankingDTOList);
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO.getId())).thenReturn("/notDefaultImage");
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO1.getId())).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<BookRankingDTO> found = bookRankingService.removeBookFromBookRank(usersId, bookRankId);
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(bookRankingDTOList.size());
        assertThat(found.contains(bookRankingDTO)).isTrue();
        assertThat(found.contains(bookRankingDTO1)).isTrue();
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findAllByUserID(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).getUsersIDFromBookRanking(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).delete(Mockito.any(BookRanking.class));
        Mockito.verify(bookRankingRepository, Mockito.times(4)).save(Mockito.any(BookRanking.class));
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findBookRankingByUser(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingRepository);
        Mockito.verify(authorRepository, Mockito.times(bookRankingDTOList.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(libroRepository, Mockito.times(bookRankingDTOList.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testRemoveBookFromBookRank_removeLast() {
        Integer usersId = 3;
        Integer bookRankId = 5;
        UsersInfo usersInfo = new UsersInfo();
        List<BookRanking> bookRankingList = getBookRankingListPartiallyFull();
        Mockito.when(usersInfoRepository.getOne(Mockito.anyInt())).thenReturn(usersInfo);
        Mockito.when(bookRankingRepository.findAllByUserID(Mockito.anyInt())).thenReturn(bookRankingList);
        Mockito.when(bookRankingRepository.getOne(bookRankId)).thenReturn(bookRankingList.get(bookRankId-1));
        Mockito.when(bookRankingRepository.getUsersIDFromBookRanking(bookRankId)).thenReturn(usersId);
        Mockito.when(bookRankingRepository.save(Mockito.any(BookRanking.class))).thenAnswer(i -> i.getArguments()[0]);
        //Essendo la parte che resituisce la lista identica al metodo findUsersBookRank, la testiamo in maniera fittizia
        List<BookRankingDTO> bookRankingDTOList = new ArrayList<>();
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Prova1");
        BookRankingDTO bookRankingDTO1 = new BookRankingDTO(2, 2, 2, "Prova2");
        bookRankingDTOList.add(bookRankingDTO);
        bookRankingDTOList.add(bookRankingDTO1);
        Mockito.when(bookRankingRepository.findBookRankingByUser(Mockito.anyInt())).thenReturn(bookRankingDTOList);
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO.getId())).thenReturn("/notDefaultImage");
        Mockito.when(libroRepository.findBookCoverPath(bookRankingDTO1.getId())).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<BookRankingDTO> found = bookRankingService.removeBookFromBookRank(usersId, bookRankId);
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(bookRankingDTOList.size());
        assertThat(found.contains(bookRankingDTO)).isTrue();
        assertThat(found.contains(bookRankingDTO1)).isTrue();
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findAllByUserID(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).getUsersIDFromBookRanking(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).delete(Mockito.any(BookRanking.class));
        Mockito.verify(bookRankingRepository, Mockito.times(0)).save(Mockito.any(BookRanking.class));
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findBookRankingByUser(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingRepository);
        Mockito.verify(authorRepository, Mockito.times(bookRankingDTOList.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(libroRepository, Mockito.times(bookRankingDTOList.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Ignore
    private static List<BookRanking> getBookRankingListPartiallyFull() {
        List<BookRanking> bookRankingList = new ArrayList<>();
        BookRanking bookRanking1 = new BookRanking();
        bookRanking1.setBookRank(1);
        bookRanking1.setId(1);
        BookRanking bookRanking2 = new BookRanking();
        bookRanking2.setBookRank(2);
        bookRanking2.setId(2);
        BookRanking bookRanking3 = new BookRanking();
        bookRanking3.setBookRank(3);
        bookRanking3.setId(3);
        BookRanking bookRanking4 = new BookRanking();
        bookRanking4.setBookRank(4);
        bookRanking4.setId(4);
        BookRanking bookRanking5 = new BookRanking();
        bookRanking5.setBookRank(5);
        bookRanking5.setId(5);
        bookRankingList.add(bookRanking1);
        bookRankingList.add(bookRanking2);
        bookRankingList.add(bookRanking3);
        bookRankingList.add(bookRanking4);
        bookRankingList.add(bookRanking5);
        return bookRankingList;
    }

    @Ignore
    private static List<Integer> getAllMyBooksInRankListPartiallyFull() {
        List<Integer> allMyBooksInRankIDs = new ArrayList<>();
        allMyBooksInRankIDs.add(1);
        allMyBooksInRankIDs.add(2);
        allMyBooksInRankIDs.add(3);
        allMyBooksInRankIDs.add(4);
        allMyBooksInRankIDs.add(5);
        return allMyBooksInRankIDs;
    }

    @Ignore
    private static List<Integer> getAllMyBooksInRankListFull() {
        List<Integer> allMyBooksInRankIDs = new ArrayList<>();
        allMyBooksInRankIDs.add(1);
        allMyBooksInRankIDs.add(2);
        allMyBooksInRankIDs.add(3);
        allMyBooksInRankIDs.add(4);
        allMyBooksInRankIDs.add(5);
        allMyBooksInRankIDs.add(6);
        allMyBooksInRankIDs.add(7);
        allMyBooksInRankIDs.add(8);
        allMyBooksInRankIDs.add(9);
        allMyBooksInRankIDs.add(10);
        return allMyBooksInRankIDs;
    }

    @Ignore
    private static List<BookRanking> getBookRankingListFull() {
        List<BookRanking> bookRankingList = new ArrayList<>();
        BookRanking bookRanking1 = new BookRanking();
        bookRanking1.setBookRank(1);
        bookRanking1.setId(1);
        BookRanking bookRanking2 = new BookRanking();
        bookRanking2.setBookRank(2);
        bookRanking2.setId(2);
        BookRanking bookRanking3 = new BookRanking();
        bookRanking3.setBookRank(3);
        bookRanking3.setId(3);
        BookRanking bookRanking4 = new BookRanking();
        bookRanking4.setBookRank(4);
        bookRanking4.setId(4);
        BookRanking bookRanking5 = new BookRanking();
        bookRanking5.setBookRank(5);
        bookRanking5.setId(5);
        BookRanking bookRanking6 = new BookRanking();
        bookRanking6.setBookRank(6);
        bookRanking6.setId(6);
        BookRanking bookRanking7 = new BookRanking();
        bookRanking7.setBookRank(7);
        bookRanking7.setId(7);
        BookRanking bookRanking8 = new BookRanking();
        bookRanking8.setBookRank(8);
        bookRanking8.setId(8);
        BookRanking bookRanking9 = new BookRanking();
        bookRanking9.setBookRank(9);
        bookRanking9.setId(9);
        BookRanking bookRanking10 = new BookRanking();
        bookRanking10.setBookRank(10);
        bookRanking10.setId(10);
        bookRankingList.add(bookRanking1);
        bookRankingList.add(bookRanking2);
        bookRankingList.add(bookRanking3);
        bookRankingList.add(bookRanking4);
        bookRankingList.add(bookRanking5);
        bookRankingList.add(bookRanking6);
        bookRankingList.add(bookRanking7);
        bookRankingList.add(bookRanking8);
        bookRankingList.add(bookRanking9);
        bookRankingList.add(bookRanking10);
        return bookRankingList;
    }
}

