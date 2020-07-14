package com.student.book_advisor.services;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.MyBooks;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.*;
import com.student.book_advisor.enums.BookShelf;
import com.student.book_advisor.services.storage.StorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
public class MyBooksServiceImplUnitTest {
    @TestConfiguration
    static class MyBooksServiceImplTestContextConfiguration {
        @Bean
        public MyBooksService genreService() {
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
}
