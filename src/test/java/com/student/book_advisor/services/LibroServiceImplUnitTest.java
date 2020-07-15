package com.student.book_advisor.services;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.LibroCardDTO;
import com.student.book_advisor.data_persistency.model.dto.LibroDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.BookRankRemovalInfoDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.OverallRatingsForBook;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.LibroFormDTO;
import com.student.book_advisor.data_persistency.model.entities.*;
import com.student.book_advisor.data_persistency.repositories.*;
import com.student.book_advisor.enums.BookShelf;
import com.student.book_advisor.security.AuthUserPrincipal;
import com.student.book_advisor.services.storage.Constants;
import com.student.book_advisor.services.storage.FileUploadDir;
import com.student.book_advisor.services.storage.StorageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
public class LibroServiceImplUnitTest {
    @TestConfiguration
    static class LibroServiceImplTestContextConfiguration {
        @Bean
        public LibroService libroService() {
            return new LibroServiceImpl();
        }
    }

    @Autowired
    private LibroService libroService;

    @MockBean
    private LibroRepository libroRepository;

    @MockBean
    private RecensioneRepository recensioneRepository;

    @MockBean
    private StorageService storageService;

    @MockBean
    private PrizeRepository prizeRepository;

    @MockBean
    private GenreJoinBookRepository genreJoinBookRepository;

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private SagaRepository sagaRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private AuthorJoinBookRepository authorJoinBookRepository;

    @MockBean
    private MyBooksRepository myBooksRepository;

    @MockBean
    private BookRankingService bookRankingService;

    @MockBean
    private BookRankingRepository bookRankingRepository;

    private SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    private Authentication authentication = Mockito.mock(Authentication.class);


    private AuthUserPrincipal authUserPrincipal = null;

    @Before
    public void setup() {
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setUsername("MarioRossi");
        usersInfo.setId(1);
        authUserPrincipal = new AuthUserPrincipal(usersInfo, null);
        //Set securityContext
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(authUserPrincipal);
    }

    @Test
    public void testFindAllBooks() {
        LibroCardDTO libroCardDTO1 = new LibroCardDTO(1, "Prova1", 4.5);
        LibroCardDTO libroCardDTO2 = new LibroCardDTO(2, "Prov2", 5.0);
        List<LibroCardDTO> libroCardDTOList = new ArrayList<>();
        libroCardDTOList.add(libroCardDTO1);
        libroCardDTOList.add(libroCardDTO2);
        Mockito.when(libroRepository.findAllBooks()).thenReturn(libroCardDTOList);
        Mockito.when(genreRepository.findGenresOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(libroRepository.findBookCoverPath(1)).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(libroRepository.findBookCoverPath(2)).thenReturn("/notDefaultCover");
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<LibroCardDTO> found = libroService.findAllBooks();
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(libroCardDTOList.size());
        Mockito.verify(libroRepository, Mockito.times(1)).findAllBooks();
        Mockito.verify(genreRepository, Mockito.times(found.size())).findGenresOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(genreRepository);
        Mockito.verify(authorRepository, Mockito.times(found.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(libroRepository, Mockito.times(found.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testFindAllBooksByGenre() {
        LibroCardDTO libroCardDTO1 = new LibroCardDTO(1, "Prova1", 4.5);
        LibroCardDTO libroCardDTO2 = new LibroCardDTO(2, "Prov2", 5.0);
        List<LibroCardDTO> libroCardDTOList = new ArrayList<>();
        libroCardDTOList.add(libroCardDTO1);
        libroCardDTOList.add(libroCardDTO2);
        List<String> genres = new ArrayList<>();
        genres.add("Romanzo");
        Mockito.when(libroRepository.findLibriByGenere(Mockito.anyString())).thenReturn(libroCardDTOList);
        Mockito.when(genreRepository.findGenresOfBook(Mockito.anyInt())).thenReturn(genres);
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(libroRepository.findBookCoverPath(1)).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(libroRepository.findBookCoverPath(2)).thenReturn("/notDefaultCover");
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<LibroCardDTO> found = libroService.findAllBooksByGenre("Romanzo");
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(libroCardDTOList.size());
        Mockito.verify(libroRepository, Mockito.times(1)).findLibriByGenere(Mockito.anyString());
        Mockito.verify(genreRepository, Mockito.times(found.size())).findGenresOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(genreRepository);
        Mockito.verify(authorRepository, Mockito.times(found.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(libroRepository, Mockito.times(found.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testFindBookById() {
        Libro book = new Libro();
        book.setTitolo("Prova");
        Mockito.when(libroRepository.getOne(Mockito.anyInt())).thenReturn(book);
        Libro found = libroService.findBookById(1);
        assertThat(found).isEqualTo(book);
        Mockito.verify(libroRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
    }

    @Test
    public void testFindBookDTOById_notDefaultImage() {
        LibroDTO book = new LibroDTO(1, "Prova", 1234, 123, "Test", "Saga", 1, 4.5, 4.0, 5.0, 4.0);
        Mockito.when(libroRepository.getBookById(1)).thenReturn(book);
        Mockito.when(libroRepository.findBookCoverPath(Mockito.anyInt())).thenReturn("/notDefaultImage");
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<String> genres = new ArrayList<>();
        String genre = "Romanzo";
        genres.add(genre);
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Escobar");
        authorOfBook.setId(1);
        authorOfBookList.add(authorOfBook);
        Mockito.when(genreRepository.findGenresOfBook(Mockito.anyInt())).thenReturn(genres);
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(authorOfBookList);
        Mockito.when(myBooksRepository.getBookShelfByBookIDAndUserID(book.getId(), authUserPrincipal.getId())).thenReturn(BookShelf.read);
        LibroDTO found = libroService.findBookDTOById(1);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(book.getId());
        assertThat(found.getSaga()).isTrue();
        assertThat(found.getBookShelf()).isEqualByComparingTo(BookShelf.read);
        assertThat(found.getGeneri()).isEqualTo(genres);
        assertThat(found.getAutori()).isEqualTo(authorOfBookList);
        Mockito.verify(libroRepository, Mockito.times(1)).getBookById(Mockito.anyInt());
        Mockito.verify(libroRepository, Mockito.times(1)).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
        Mockito.verify(genreRepository, Mockito.times(1)).findGenresOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(genreRepository);
        Mockito.verify(authorRepository, Mockito.times(1)).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(myBooksRepository, Mockito.times(1)).getBookShelfByBookIDAndUserID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(myBooksRepository);
    }

    @Test
    public void testNewBook_partOfSaga_authorPresent() {
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setAnnoPubblicazione(1234);
        libroFormDTO.setPagine(123);
        libroFormDTO.setTitolo("Prova");
        libroFormDTO.setSinossi("sinossi");
        libroFormDTO.setSaga(true);
        libroFormDTO.setNumInSaga(1);
        libroFormDTO.setTitoloSaga("Saga");
        List<String> genres = new ArrayList<>();
        genres.add("Romanzo");
        libroFormDTO.setGeneri(genres);
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Escobar");
        authorOfBook.setId(1);
        authorOfBookList.add(authorOfBook);
        libroFormDTO.setAutori(authorOfBookList);
        Mockito.when(libroRepository.save(Mockito.any(Libro.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(genreRepository.findByGenre(Mockito.anyString())).thenReturn(new Genre());
        Mockito.when(genreJoinBookRepository.save(Mockito.any(GenreJoinBook.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(sagaRepository.save(Mockito.any(Saga.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(authorRepository.getOne(Mockito.anyInt())).thenReturn(new Author());
        Mockito.when(authorJoinBookRepository.save(Mockito.any(AuthorJoinBook.class))).thenAnswer(i -> i.getArguments()[0]);
        Libro added = libroService.newBook(libroFormDTO);
        assertThat(added).isNotNull();
        assertThat(added.getTitolo()).isEqualTo(libroFormDTO.getTitolo());
        Mockito.verify(libroRepository, Mockito.times(1)).save(Mockito.any(Libro.class));
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(genreRepository, Mockito.times(genres.size())).findByGenre(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(genreRepository);
        Mockito.verify(genreJoinBookRepository, Mockito.times(genres.size())).save(Mockito.any(GenreJoinBook.class));
        Mockito.verifyNoMoreInteractions(genreJoinBookRepository);
        Mockito.verify(sagaRepository, Mockito.times(1)).save(Mockito.any(Saga.class));
        Mockito.verifyNoMoreInteractions(sagaRepository);
        Mockito.verify(authorRepository, Mockito.times(authorOfBookList.size())).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(authorJoinBookRepository, Mockito.times(authorOfBookList.size())).save(Mockito.any(AuthorJoinBook.class));
        Mockito.verifyNoMoreInteractions(authorJoinBookRepository);
    }

    @Test
    public void testNewBook_notPartOfSaga_authorNotPresent() {
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setAnnoPubblicazione(1234);
        libroFormDTO.setPagine(123);
        libroFormDTO.setTitolo("Prova");
        libroFormDTO.setSinossi("sinossi");
        libroFormDTO.setSaga(false);
        List<String> genres = new ArrayList<>();
        genres.add("Romanzo");
        libroFormDTO.setGeneri(genres);
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Escobar");
        authorOfBook.setId(1);
        authorOfBookList.add(authorOfBook);
        libroFormDTO.setAutori(authorOfBookList);
        Mockito.when(libroRepository.save(Mockito.any(Libro.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(genreRepository.findByGenre(Mockito.anyString())).thenReturn(new Genre());
        Mockito.when(genreJoinBookRepository.save(Mockito.any(GenreJoinBook.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(authorRepository.getOne(Mockito.anyInt())).thenReturn(null);
        Mockito.when(authorJoinBookRepository.save(Mockito.any(AuthorJoinBook.class))).thenAnswer(i -> i.getArguments()[0]);
        assertThatThrownBy(() -> libroService.newBook(libroFormDTO))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("This author doesn't exist!");
        Mockito.verify(libroRepository, Mockito.times(1)).save(Mockito.any(Libro.class));
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(genreRepository, Mockito.times(genres.size())).findByGenre(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(genreRepository);
        Mockito.verify(genreJoinBookRepository, Mockito.times(genres.size())).save(Mockito.any(GenreJoinBook.class));
        Mockito.verifyNoMoreInteractions(genreJoinBookRepository);
        Mockito.verifyNoInteractions(sagaRepository);
        Mockito.verify(authorRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verifyNoInteractions(authorJoinBookRepository);
    }

    //Presentiamo solo alcuni dei test case di copertura per questo metodo
    @Test
    public void testUpdateBook_bookNull() {
        Mockito.when(libroRepository.getOne(Mockito.anyInt())).thenReturn(null);
        Libro updated = libroService.updateBook(new LibroFormDTO(), 1);
        assertThat(updated).isNull();
        Mockito.verify(libroRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verifyNoInteractions(genreRepository);
        Mockito.verifyNoInteractions(authorRepository);
        Mockito.verifyNoInteractions(authorJoinBookRepository);
        Mockito.verifyNoInteractions(genreJoinBookRepository);
        Mockito.verifyNoInteractions(sagaRepository);
    }

    @Test
    public void testUpdateBook_sagaToRemove() {
        Integer bookId = 1;
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("Prova");
        libroFormDTO.setSaga(false);
        libroFormDTO.setSinossi("sinossi");
        libroFormDTO.setPagine(123);
        libroFormDTO.setAnnoPubblicazione(1234);
        List<String> genresUpdated = new ArrayList<>();
        String commonGenre = "Romanzo";
        genresUpdated.add(commonGenre);
        genresUpdated.add("Fantasy");
        libroFormDTO.setGeneri(genresUpdated);
        List<String> oldGenres = new ArrayList<>();
        oldGenres.add(commonGenre);
        oldGenres.add("Fantascienza");
        Genre genreToRemove = new Genre();
        genreToRemove.setGenre("Fantascienza");
        Genre genreToAdd = new Genre();
        genreToAdd.setGenre("Fantasy");
        List<AuthorOfBook> authorsUpdated = new ArrayList<>();
        AuthorOfBook commonAuthor = new AuthorOfBook();
        commonAuthor.setId(1);
        commonAuthor.setAuthorsFullname("Pablo Escobar");
        AuthorOfBook newAuthor = new AuthorOfBook();
        newAuthor.setAuthorsFullname("Mario Rossi");
        newAuthor.setId(2);
        authorsUpdated.add(commonAuthor);
        authorsUpdated.add(newAuthor);
        libroFormDTO.setAutori(authorsUpdated);
        AuthorOfBook oldAuthor = new AuthorOfBook();
        oldAuthor.setId(3);
        oldAuthor.setAuthorsFullname("Brandon Sanderson");
        List<AuthorOfBook> oldAuthors = new ArrayList<>();
        oldAuthors.add(commonAuthor);
        oldAuthors.add(oldAuthor);
        Libro book = new Libro();
        Author authorToRemove = new Author();
        authorToRemove.setAuthorsFullname(oldAuthor.getAuthorsFullname());
        Author authorToAdd = new Author();
        authorToAdd.setAuthorsFullname(newAuthor.getAuthorsFullname());
        Mockito.when(libroRepository.getOne(Mockito.anyInt())).thenReturn(book);
        Mockito.when(libroRepository.save(Mockito.any(Libro.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(genreRepository.findGenresOfBook(bookId)).thenReturn(oldGenres);
        Mockito.when(genreRepository.findByGenre(genreToRemove.getGenre())).thenReturn(genreToRemove);
        Mockito.when(genreRepository.findByGenre(genreToAdd.getGenre())).thenReturn(genreToAdd);
        Mockito.when(genreJoinBookRepository.findByGenreAndBookID(genreToRemove.getGenre(), bookId)).thenReturn(new GenreJoinBook());
        Mockito.when(genreJoinBookRepository.findByGenreAndBookID(genreToAdd.getGenre(), bookId)).thenReturn(null);
        Mockito.when(genreJoinBookRepository.save(Mockito.any(GenreJoinBook.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(sagaRepository.findByBook(Mockito.any(Libro.class))).thenReturn(new Saga());
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(oldAuthors);
        Mockito.when(authorRepository.getOne(oldAuthor.getId())).thenReturn(authorToRemove);
        Mockito.when(authorRepository.getOne(newAuthor.getId())).thenReturn(authorToAdd);
        Mockito.when(authorJoinBookRepository.findByAuthorAndBook(authorToRemove, book)).thenReturn(new AuthorJoinBook());
        Mockito.when(authorJoinBookRepository.findByAuthorAndBook(authorToAdd, book)).thenReturn(null);
        Mockito.when(authorJoinBookRepository.save(Mockito.any(AuthorJoinBook.class))).thenAnswer(i -> i.getArguments()[0]);
        Libro updated = libroService.updateBook(libroFormDTO, bookId);
        assertThat(updated).isNotNull();
        assertThat(updated.getTitolo()).isEqualTo(libroFormDTO.getTitolo());
        Mockito.verify(libroRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verify(libroRepository, Mockito.times(1)).save(Mockito.any(Libro.class));
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(genreRepository, Mockito.times(1)).findGenresOfBook(Mockito.anyInt());
        Mockito.verify(genreRepository, Mockito.times(2)).findByGenre(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(genreRepository);
        Mockito.verify(genreJoinBookRepository, Mockito.times(2)).findByGenreAndBookID(Mockito.anyString(), Mockito.anyInt());
        Mockito.verify(genreJoinBookRepository, Mockito.times(1)).delete(Mockito.any(GenreJoinBook.class));
        Mockito.verify(genreJoinBookRepository, Mockito.times(1)).save(Mockito.any(GenreJoinBook.class));
        Mockito.verifyNoMoreInteractions(genreJoinBookRepository);
        Mockito.verify(sagaRepository, Mockito.times(1)).findByBook(Mockito.any(Libro.class));
        Mockito.verify(sagaRepository, Mockito.times(1)).delete(Mockito.any(Saga.class));
        Mockito.verifyNoMoreInteractions(sagaRepository);
        Mockito.verify(authorRepository, Mockito.times(1)).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verify(authorRepository, Mockito.times(2)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(authorJoinBookRepository, Mockito.times(2)).findByAuthorAndBook(Mockito.any(Author.class), Mockito.any(Libro.class));
        Mockito.verify(authorJoinBookRepository, Mockito.times(1)).delete(Mockito.any(AuthorJoinBook.class));
        Mockito.verify(authorJoinBookRepository, Mockito.times(1)).save(Mockito.any(AuthorJoinBook.class));
        Mockito.verifyNoMoreInteractions(authorJoinBookRepository);
    }

    @Test
    public void testUpdateBook_sagaToAdd() {
        Integer bookId = 1;
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("Prova");
        libroFormDTO.setSaga(true);
        libroFormDTO.setSinossi("sinossi");
        libroFormDTO.setPagine(123);
        libroFormDTO.setAnnoPubblicazione(1234);
        libroFormDTO.setNumInSaga(2);
        libroFormDTO.setTitoloSaga("Saga");
        List<String> genresUpdated = new ArrayList<>();
        String commonGenre = "Romanzo";
        genresUpdated.add(commonGenre);
        genresUpdated.add("Fantasy");
        libroFormDTO.setGeneri(genresUpdated);
        List<String> oldGenres = new ArrayList<>();
        oldGenres.add(commonGenre);
        oldGenres.add("Fantascienza");
        Genre genreToRemove = new Genre();
        genreToRemove.setGenre("Fantascienza");
        Genre genreToAdd = new Genre();
        genreToAdd.setGenre("Fantasy");
        List<AuthorOfBook> authorsUpdated = new ArrayList<>();
        AuthorOfBook commonAuthor = new AuthorOfBook();
        commonAuthor.setId(1);
        commonAuthor.setAuthorsFullname("Pablo Escobar");
        AuthorOfBook newAuthor = new AuthorOfBook();
        newAuthor.setAuthorsFullname("Mario Rossi");
        newAuthor.setId(2);
        authorsUpdated.add(commonAuthor);
        authorsUpdated.add(newAuthor);
        libroFormDTO.setAutori(authorsUpdated);
        AuthorOfBook oldAuthor = new AuthorOfBook();
        oldAuthor.setId(3);
        oldAuthor.setAuthorsFullname("Brandon Sanderson");
        List<AuthorOfBook> oldAuthors = new ArrayList<>();
        oldAuthors.add(commonAuthor);
        oldAuthors.add(oldAuthor);
        Libro book = new Libro();
        Author authorToRemove = new Author();
        authorToRemove.setAuthorsFullname(oldAuthor.getAuthorsFullname());
        Author authorToAdd = new Author();
        authorToAdd.setAuthorsFullname(newAuthor.getAuthorsFullname());
        Mockito.when(libroRepository.getOne(Mockito.anyInt())).thenReturn(book);
        Mockito.when(libroRepository.save(Mockito.any(Libro.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(genreRepository.findGenresOfBook(bookId)).thenReturn(oldGenres);
        Mockito.when(genreRepository.findByGenre(genreToRemove.getGenre())).thenReturn(genreToRemove);
        Mockito.when(genreRepository.findByGenre(genreToAdd.getGenre())).thenReturn(genreToAdd);
        Mockito.when(genreJoinBookRepository.findByGenreAndBookID(genreToRemove.getGenre(), bookId)).thenReturn(new GenreJoinBook());
        Mockito.when(genreJoinBookRepository.findByGenreAndBookID(genreToAdd.getGenre(), bookId)).thenReturn(null);
        Mockito.when(genreJoinBookRepository.save(Mockito.any(GenreJoinBook.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(sagaRepository.findByBook(Mockito.any(Libro.class))).thenReturn(null);
        Mockito.when(sagaRepository.save(Mockito.any(Saga.class))).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(oldAuthors);
        Mockito.when(authorRepository.getOne(oldAuthor.getId())).thenReturn(authorToRemove);
        Mockito.when(authorRepository.getOne(newAuthor.getId())).thenReturn(authorToAdd);
        Mockito.when(authorJoinBookRepository.findByAuthorAndBook(authorToRemove, book)).thenReturn(new AuthorJoinBook());
        Mockito.when(authorJoinBookRepository.findByAuthorAndBook(authorToAdd, book)).thenReturn(null);
        Mockito.when(authorJoinBookRepository.save(Mockito.any(AuthorJoinBook.class))).thenAnswer(i -> i.getArguments()[0]);
        Libro updated = libroService.updateBook(libroFormDTO, bookId);
        assertThat(updated).isNotNull();
        assertThat(updated.getTitolo()).isEqualTo(libroFormDTO.getTitolo());
        Mockito.verify(libroRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verify(libroRepository, Mockito.times(1)).save(Mockito.any(Libro.class));
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(genreRepository, Mockito.times(1)).findGenresOfBook(Mockito.anyInt());
        Mockito.verify(genreRepository, Mockito.times(2)).findByGenre(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(genreRepository);
        Mockito.verify(genreJoinBookRepository, Mockito.times(2)).findByGenreAndBookID(Mockito.anyString(), Mockito.anyInt());
        Mockito.verify(genreJoinBookRepository, Mockito.times(1)).delete(Mockito.any(GenreJoinBook.class));
        Mockito.verify(genreJoinBookRepository, Mockito.times(1)).save(Mockito.any(GenreJoinBook.class));
        Mockito.verifyNoMoreInteractions(genreJoinBookRepository);
        Mockito.verify(sagaRepository, Mockito.times(1)).findByBook(Mockito.any(Libro.class));
        Mockito.verify(sagaRepository, Mockito.times(1)).save(Mockito.any(Saga.class));
        Mockito.verifyNoMoreInteractions(sagaRepository);
        Mockito.verify(authorRepository, Mockito.times(1)).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verify(authorRepository, Mockito.times(2)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(authorJoinBookRepository, Mockito.times(2)).findByAuthorAndBook(Mockito.any(Author.class), Mockito.any(Libro.class));
        Mockito.verify(authorJoinBookRepository, Mockito.times(1)).delete(Mockito.any(AuthorJoinBook.class));
        Mockito.verify(authorJoinBookRepository, Mockito.times(1)).save(Mockito.any(AuthorJoinBook.class));
        Mockito.verifyNoMoreInteractions(authorJoinBookRepository);
    }

    @Test
    public void testDeleteBook_bookNull() {
        Mockito.when(libroRepository.getOne(Mockito.anyInt())).thenReturn(null);
        libroService.deleteBook(1);
        Mockito.verify(libroRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verifyNoInteractions(bookRankingRepository);
    }

    @Test
    public void testDeleteBook_bookNotNull() {
        Libro book = new Libro();
        book.setBookCoverPath("/notDefaultImage");
        Mockito.when(libroRepository.getOne(Mockito.anyInt())).thenReturn(book);
        List<BookRankRemovalInfoDTO> bookRankRemovalInfoDTOList = new ArrayList<>();
        BookRankRemovalInfoDTO bookRankRemovalInfoDTO = new BookRankRemovalInfoDTO(1, 2);
        bookRankRemovalInfoDTOList.add(bookRankRemovalInfoDTO);
        Mockito.when(bookRankingRepository.getAllBookRanksByBookID(Mockito.anyInt())).thenReturn(bookRankRemovalInfoDTOList);
        Mockito.when(bookRankingService.removeBookFromBookRank(Mockito.anyInt(), Mockito.anyInt())).thenReturn(new ArrayList<>());
        libroService.deleteBook(1);
        Mockito.verify(libroRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).getAllBookRanksByBookID(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingRepository);
        Mockito.verify(bookRankingService, Mockito.times(bookRankRemovalInfoDTOList.size())).removeBookFromBookRank(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingService);
        Mockito.verify(libroRepository, Mockito.times(1)).delete(Mockito.any(Libro.class));
        Mockito.verifyNoMoreInteractions(libroRepository);
    }

    @Test
    public void testIsTitleUnique_True() {
        Mockito.when(libroRepository.countAllByTitolo(Mockito.anyString())).thenReturn(0);
        Boolean unique = libroService.isTitleUnique("Prova");
        assertThat(unique).isTrue();
        Mockito.verify(libroRepository, Mockito.times(1)).countAllByTitolo(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(libroRepository);
    }

    @Test
    public void testIsTitleUnique_False() {
        Mockito.when(libroRepository.countAllByTitolo(Mockito.anyString())).thenReturn(1);
        Boolean unique = libroService.isTitleUnique("Prova");
        assertThat(unique).isFalse();
        Mockito.verify(libroRepository, Mockito.times(1)).countAllByTitolo(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(libroRepository);
    }

    @Test
    public void testUpdateBooksCoverPhoto() {
        Mockito.when(storageService.serve(Mockito.nullable(String.class), Mockito.any(FileUploadDir.class))).thenReturn("test");
        Mockito.when(storageService.store(Mockito.any(MultipartFile.class), Mockito.any(FileUploadDir.class), Mockito.nullable(String.class))).thenReturn("test");
        MultipartFile mf = new MockMultipartFile("test", "test".getBytes());
        Libro book = new Libro();
        Mockito.when(libroRepository.save(Mockito.any(Libro.class))).thenAnswer(i -> i.getArguments()[0]);
        String src = libroService.updateBooksCoverPhoto(mf, book);
        assertThat(src).isNotNull();
        assertThat(src).isEqualTo("{ \"img\":\"test\"}");
        Mockito.verify(storageService, Mockito.times(1)).store(Mockito.any(MultipartFile.class), Mockito.any(FileUploadDir.class), Mockito.nullable(String.class));
        Mockito.verify(libroRepository, Mockito.times(1)).save(Mockito.any(Libro.class));
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.nullable(String.class), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testFindBooksContainingTitolo() {
        LibroCardDTO libroCardDTO1 = new LibroCardDTO(1, "Prova1", 4.5);
        LibroCardDTO libroCardDTO2 = new LibroCardDTO(2, "Prova2", 5.0);
        List<LibroCardDTO> libroCardDTOList = new ArrayList<>();
        libroCardDTOList.add(libroCardDTO1);
        libroCardDTOList.add(libroCardDTO2);
        Mockito.when(libroRepository.findAllBooksContainingTitolo("Prova")).thenReturn(libroCardDTOList);
        Mockito.when(genreRepository.findGenresOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(libroRepository.findBookCoverPath(1)).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(libroRepository.findBookCoverPath(2)).thenReturn("/notDefaultCover");
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<LibroCardDTO> found = libroService.findBooksContainingTitolo("Prova");
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(libroCardDTOList.size());
        Mockito.verify(libroRepository, Mockito.times(1)).findAllBooksContainingTitolo(Mockito.anyString());
        Mockito.verify(genreRepository, Mockito.times(found.size())).findGenresOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(genreRepository);
        Mockito.verify(authorRepository, Mockito.times(found.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(libroRepository, Mockito.times(found.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testFindAllBooksByTitoloSagaExcludingCurrent() {
        LibroCardDTO libroCardDTO1 = new LibroCardDTO(1, "Prova1", 4.5);
        LibroCardDTO libroCardDTO2 = new LibroCardDTO(2, "Prova2", 5.0);
        List<LibroCardDTO> libroCardDTOList = new ArrayList<>();
        libroCardDTOList.add(libroCardDTO1);
        libroCardDTOList.add(libroCardDTO2);
        Mockito.when(libroRepository.findAllBooksByTitoloSagaExcludingCurrent(Mockito.anyString(), Mockito.anyInt())).thenReturn(libroCardDTOList);
        Mockito.when(genreRepository.findGenresOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(libroRepository.findBookCoverPath(1)).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(libroRepository.findBookCoverPath(2)).thenReturn("/notDefaultCover");
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<LibroCardDTO> found = libroService.findAllBooksByTitoloSagaExcludingCurrent("Saga", 3);
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(libroCardDTOList.size());
        Mockito.verify(libroRepository, Mockito.times(1)).findAllBooksByTitoloSagaExcludingCurrent(Mockito.anyString(), Mockito.anyInt());
        Mockito.verify(genreRepository, Mockito.times(found.size())).findGenresOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(genreRepository);
        Mockito.verify(authorRepository, Mockito.times(found.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(libroRepository, Mockito.times(found.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testGetBookOverallRatings() {
        OverallRatingsForBook overallRatingsForBook = new OverallRatingsForBook(4.5, 3.4, 5.0, 4.7);
        Mockito.when(recensioneRepository.getAverageRatingsOfBook(Mockito.anyInt())).thenReturn(overallRatingsForBook);
        OverallRatingsForBook found = libroService.getBookOverallRatings(1);
        assertThat(found).isEqualTo(overallRatingsForBook);
        Mockito.verify(recensioneRepository, Mockito.times(1)).getAverageRatingsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneRepository);
    }

    @Test
    public void testFindAllBooksByAuthor() {
        LibroCardDTO libroCardDTO1 = new LibroCardDTO(1, "Prova1", 4.5);
        LibroCardDTO libroCardDTO2 = new LibroCardDTO(2, "Prova2", 5.0);
        List<LibroCardDTO> libroCardDTOList = new ArrayList<>();
        libroCardDTOList.add(libroCardDTO1);
        libroCardDTOList.add(libroCardDTO2);
        Mockito.when(libroRepository.findAllBooksByAuthor(Mockito.anyString())).thenReturn(libroCardDTOList);
        Mockito.when(genreRepository.findGenresOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(libroRepository.findBookCoverPath(1)).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(libroRepository.findBookCoverPath(2)).thenReturn("/notDefaultCover");
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<LibroCardDTO> found = libroService.findAllBooksByAuthor("Pablo Escobar");
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(libroCardDTOList.size());
        Mockito.verify(libroRepository, Mockito.times(1)).findAllBooksByAuthor(Mockito.anyString());
        Mockito.verify(genreRepository, Mockito.times(found.size())).findGenresOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(genreRepository);
        Mockito.verify(authorRepository, Mockito.times(found.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(libroRepository, Mockito.times(found.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }
}
