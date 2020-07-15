package com.student.book_advisor.services;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.BookRankingDTO;
import com.student.book_advisor.data_persistency.model.dto.MyBooksDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.MyBooksReadDTO;
import com.student.book_advisor.data_persistency.model.entities.*;
import com.student.book_advisor.data_persistency.repositories.*;
import com.student.book_advisor.enums.BookShelf;
import com.student.book_advisor.services.storage.Constants;
import com.student.book_advisor.services.storage.FileUploadDir;
import com.student.book_advisor.services.storage.StorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
public class MyBooksServiceImplUnitTest {
    @TestConfiguration
    static class MyBooksServiceImplTestContextConfiguration {
        @Bean
        public MyBooksService myBooksService() {
            return new MyBooksServiceImpl();
        }
    }

    @Autowired
    private MyBooksService myBooksService;

    @MockBean
    private MyBooksRepository myBooksRepository;

    @MockBean
    private UsersInfoRepository usersInfoRepository;

    @MockBean
    private LibroRepository libroRepository;

    @MockBean
    private BookRankingRepository bookRankingRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private StorageService storageService;

    @MockBean
    private BookRankingService bookRankingService;

    @Test
    public void testAddToShelf_MyBookNotPresent() {
        Mockito.when(myBooksRepository.getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.getOne(Mockito.anyInt())).thenReturn(usersInfo);
        Libro book = new Libro();
        Mockito.when(libroRepository.getOne(Mockito.anyInt())).thenReturn(book);
        MyBooks myBooks = new MyBooks();
        myBooks.setShelfType(BookShelf.read);
        Mockito.when(myBooksRepository.save(Mockito.any(MyBooks.class))).thenReturn(myBooks);
        String shelf = myBooksService.addToShelf(1, 2, BookShelf.read);
        assertThat(shelf).isNotNull();
        assertThat(shelf).isEqualTo(myBooks.getShelfType().toString());
        Mockito.verify(myBooksRepository, Mockito.times(1)).getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(libroRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(myBooksRepository, Mockito.times(1)).save(Mockito.any(MyBooks.class));
        Mockito.verifyNoMoreInteractions(myBooksRepository);
    }

    @Test
    public void testAddToShelf_MyBookNotPresent_UserNotPresent() {
        Mockito.when(myBooksRepository.getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);
        Mockito.when(usersInfoRepository.getOne(Mockito.anyInt())).thenReturn(null);
        assertThatThrownBy(() -> myBooksService.addToShelf(1, 2, BookShelf.read))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("User doesn't exist!");
        Mockito.verify(myBooksRepository, Mockito.times(1)).getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verifyNoMoreInteractions(myBooksRepository);
        Mockito.verifyNoInteractions(libroRepository);
    }

    @Test
    public void testAddToShelf_MyBookNotPresent_BookNotPresent() {
        Mockito.when(myBooksRepository.getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.getOne(Mockito.anyInt())).thenReturn(usersInfo);
        Mockito.when(libroRepository.getOne(Mockito.anyInt())).thenReturn(null);
        assertThatThrownBy(() -> myBooksService.addToShelf(1, 2, BookShelf.read))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("Book doesn't exist!");
        Mockito.verify(myBooksRepository, Mockito.times(1)).getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(libroRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verifyNoMoreInteractions(myBooksRepository);
    }

    @Test
    public void testAddToShelf_MyBookAlreadyPresent() {
        MyBooks myBooks = new MyBooks();
        Mockito.when(myBooksRepository.getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt())).thenReturn(myBooks);
        assertThatThrownBy(() -> myBooksService.addToShelf(1, 2, BookShelf.read))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("Book already part of MyBooks");
        Mockito.verify(myBooksRepository, Mockito.times(1)).getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(myBooksRepository);
        Mockito.verifyNoInteractions(libroRepository);
        Mockito.verifyNoInteractions(usersInfoRepository);
    }

    @Test
    public void testUpdateShelf_MyBookNull() {
        Mockito.when(myBooksRepository.getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);
        assertThatThrownBy(() -> myBooksService.updateShelf(1, 2, BookShelf.read))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("Book isn't part of user's mybooks");
        Mockito.verify(myBooksRepository, Mockito.times(1)).getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(myBooksRepository);
        Mockito.verifyNoInteractions(bookRankingService);
        Mockito.verifyNoInteractions(bookRankingRepository);
    }

    @Test
    public void testUpdateShelf_OldShelfNotRead() {
        MyBooks myBooks = new MyBooks();
        myBooks.setShelfType(BookShelf.reading);
        Mockito.when(myBooksRepository.getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt())).thenReturn(myBooks);
        Mockito.when(myBooksRepository.save(Mockito.any(MyBooks.class))).thenReturn(myBooks);
        Boolean modifiedRank = myBooksService.updateShelf(1, 2, BookShelf.read);
        assertThat(modifiedRank).isFalse();
        Mockito.verify(myBooksRepository, Mockito.times(1)).getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(myBooksRepository, Mockito.times(1)).save(Mockito.any(MyBooks.class));
        Mockito.verifyNoMoreInteractions(myBooksRepository);
        Mockito.verifyNoInteractions(bookRankingService);
        Mockito.verifyNoInteractions(bookRankingRepository);
    }

    @Test
    public void testUpdateShelf_OldShelfReadAndBookRankToModify() {
        MyBooks myBooks = new MyBooks();
        myBooks.setShelfType(BookShelf.read);
        myBooks.setId(1);
        Mockito.when(myBooksRepository.getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt())).thenReturn(myBooks);
        Mockito.when(myBooksRepository.save(Mockito.any(MyBooks.class))).thenReturn(myBooks);
        BookRanking bookRanking = new BookRanking();
        bookRanking.setBookRank(1);
        bookRanking.setId(2);
        Mockito.when(bookRankingRepository.getBookRankingByMyBooksID(Mockito.anyInt())).thenReturn(bookRanking);
        Mockito.when(bookRankingService.removeBookFromBookRank(Mockito.anyInt(), Mockito.anyInt())).thenReturn(new ArrayList<BookRankingDTO>());
        Boolean modifiedRank = myBooksService.updateShelf(1, 2, BookShelf.reading);
        assertThat(modifiedRank).isTrue();
        Mockito.verify(myBooksRepository, Mockito.times(1)).getByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(myBooksRepository, Mockito.times(1)).save(Mockito.any(MyBooks.class));
        Mockito.verifyNoMoreInteractions(myBooksRepository);
        Mockito.verify(bookRankingRepository, Mockito.times(1)).getBookRankingByMyBooksID(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingRepository);
        Mockito.verify(bookRankingService, Mockito.times(1)).removeBookFromBookRank(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingService);
    }

    @Test
    public void testFindAllMyBooks() {
        MyBooksDTO myBooksDTO1 = new MyBooksDTO(1, 2, "Test1", BookShelf.read, 2, 3.4);
        MyBooksDTO myBooksDTO2 = new MyBooksDTO(2, 3, "Test2", BookShelf.reading, 2, 4.0);
        List<MyBooksDTO> myBooksDTOList = new ArrayList<MyBooksDTO>();
        myBooksDTOList.add(myBooksDTO1);
        myBooksDTOList.add(myBooksDTO2);
        String genre = "Romanzo";
        List<String> genres = new ArrayList<>();
        genres.add(genre);
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setId(2);
        authorOfBook.setAuthorsFullname("Pablo");
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        authorOfBookList.add(authorOfBook);
        Mockito.when(myBooksRepository.getMyBooksByUserID(2)).thenReturn(myBooksDTOList);
        Mockito.when(genreRepository.findGenresOfBook(Mockito.anyInt())).thenReturn(genres);
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(authorOfBookList);
        Mockito.when(libroRepository.findBookCoverPath(2)).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(libroRepository.findBookCoverPath(3)).thenReturn("/notDefault");
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<MyBooksDTO> found = myBooksService.findAllMyBooks(2);
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(2);
        for(MyBooksDTO myBooksDTO : found) {
            assertThat(myBooksDTO.getAuthors()).isEqualTo(authorOfBookList);
            assertThat(myBooksDTO.getGenres()).isEqualTo(genres);
            if(!myBooksDTO.getCoverImage().equals(Constants.DEF_BOOK_COVER)) {
                assertThat(myBooksDTO.getCoverImage().equals("/test"));
            }
        }
        Mockito.verify(myBooksRepository, Mockito.times(1)).getMyBooksByUserID(Mockito.anyInt());
        Mockito.verify(genreRepository, Mockito.times(found.size())).findGenresOfBook(Mockito.anyInt());
        Mockito.verify(authorRepository, Mockito.times(found.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verify(libroRepository, Mockito.times(found.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(myBooksRepository);
        Mockito.verifyNoMoreInteractions(genreRepository);
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verifyNoMoreInteractions(storageService);


    }

    @Test
    public void testFindAllMyBooksRead() {
        List<MyBooksReadDTO> myBooksReadDTOList = new ArrayList<>();
        MyBooksReadDTO myBooksReadDTO = new MyBooksReadDTO(1, "Test");
        myBooksReadDTOList.add(myBooksReadDTO);
        Mockito.when(myBooksRepository.getAllMyBooksReadButNotInRank(Mockito.anyInt())).thenReturn(myBooksReadDTOList);
        List<MyBooksReadDTO> found = myBooksService.findAllMyBooksRead(2);
        assertThat(found).isNotEmpty();
        assertThat(found).isEqualTo(myBooksReadDTOList);
        Mockito.verify(myBooksRepository, Mockito.times(1)).getAllMyBooksReadButNotInRank(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(myBooksRepository);

    }
}
