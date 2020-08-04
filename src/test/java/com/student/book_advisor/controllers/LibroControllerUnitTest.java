package com.student.book_advisor.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.student.book_advisor.data_persistency.model.dto.LibroCardDTO;
import com.student.book_advisor.data_persistency.model.dto.LibroDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.OverallRatingsForBook;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.LibroFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Authorities;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.AuthoritiesRepository;
import com.student.book_advisor.data_persistency.repositories.UsersInfoRepository;
import com.student.book_advisor.security.JwtTokenProvider;
import com.student.book_advisor.security.SecurityConfiguration;
import com.student.book_advisor.security.UserDetailsServiceImpl;
import com.student.book_advisor.security.redis.RedisUtil;
import com.student.book_advisor.services.LibroService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = LibroController.class)
@ContextConfiguration(classes = {SecurityConfiguration.class, JwtTokenProvider.class, RedisUtil.class, UserDetailsServiceImpl.class, LibroController.class})
public class LibroControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private LibroService libroService;

    @MockBean
    private UsersInfoRepository usersInfoRepository;

    @MockBean
    private AuthoritiesRepository authoritiesRepository;

    private String adminToken = null;
    private String userToken = null;

    @Before
    public void setup() {
        UsersInfo adminUsersInfo = new UsersInfo();
        adminUsersInfo.setId(1);
        adminUsersInfo.setUsername("adminUser");
        adminUsersInfo.setPassword("password");

        List<String> adminRoles = new ArrayList<>();
        adminRoles.add("ROLE_ADMIN");
        adminRoles.add("ROLE_USER");

        adminToken = jwtTokenProvider.createToken(adminUsersInfo.getUsername(), adminUsersInfo.getId(), adminRoles);
        Mockito.when(usersInfoRepository.findByUsername(adminUsersInfo.getUsername())).thenReturn(adminUsersInfo);
        List<Authorities> authoritiesList = new ArrayList<>();
        Authorities authUser  = new Authorities();
        authUser.setAuthority("USER");
        Authorities authAdmin = new Authorities();
        authAdmin.setAuthority("ADMIN");
        authoritiesList.add(authUser);
        authoritiesList.add(authAdmin);
        Mockito.when(authoritiesRepository.findAllByUserID(adminUsersInfo.getId())).thenReturn(authoritiesList);

        UsersInfo userUsersInfo = new UsersInfo();
        userUsersInfo.setId(2);
        userUsersInfo.setUsername("userUser");
        userUsersInfo.setPassword("password");
        List<String> userRoles = new ArrayList<>();
        userRoles.add("ROLE_USER");
        userToken = jwtTokenProvider.createToken(userUsersInfo.getUsername(), userUsersInfo.getId(), userRoles);
        Mockito.when(usersInfoRepository.findByUsername(userUsersInfo.getUsername())).thenReturn(userUsersInfo);
        List<Authorities> userAuthoritiesList = new ArrayList<>();
        userAuthoritiesList.add(authUser);
        Mockito.when(authoritiesRepository.findAllByUserID(userUsersInfo.getId())).thenReturn(userAuthoritiesList);
    }

    @Test
    public void testGetAllBooksByParam_genre() throws Exception {
        String genere = "Romanzo";
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        List<LibroCardDTO> libroCardDTOList = new ArrayList<>();
        LibroCardDTO libroCardDTO = new LibroCardDTO(1, "Prova", 4.5);
        List<String> genres = new ArrayList<>();
        genres.add(genere);
        libroCardDTO.setGeneri(genres);
        libroCardDTOList.add(libroCardDTO);
        Mockito.when(libroService.findAllBooksByGenre(genere)).thenReturn(libroCardDTOList);
        mockMvc.perform(get("/libri")
                .param("genere", genere)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(libroCardDTO.getId())))
                .andExpect(jsonPath("$[0].titolo", is(libroCardDTO.getTitolo())))
                .andExpect(jsonPath("$[0].overallRating", is(libroCardDTO.getOverallRating())))
                .andExpect(jsonPath("$[0].generi", containsInAnyOrder(genere)));
        Mockito.verify(libroService, Mockito.times(1)).findAllBooksByGenre(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testGetAllBooksByParam_titolo() throws Exception {
        String titolo = "Prova";
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        List<LibroCardDTO> libroCardDTOList = new ArrayList<>();
        LibroCardDTO libroCardDTO = new LibroCardDTO(1, "Prova", 4.5);
        libroCardDTOList.add(libroCardDTO);
        Mockito.when(libroService.findBooksContainingTitolo(titolo)).thenReturn(libroCardDTOList);
        mockMvc.perform(get("/libri")
                .param("titolo", titolo)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(libroCardDTO.getId())))
                .andExpect(jsonPath("$[0].titolo", is(libroCardDTO.getTitolo())))
                .andExpect(jsonPath("$[0].overallRating", is(libroCardDTO.getOverallRating())));
        Mockito.verify(libroService, Mockito.times(1)).findBooksContainingTitolo(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testGetAllBooksByParam_titoloSagaAndBookId() throws Exception {
        String titoloSaga = "Saga";
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        List<LibroCardDTO> libroCardDTOList = new ArrayList<>();
        LibroCardDTO libroCardDTO = new LibroCardDTO(bookID, "Prova", 4.5);
        libroCardDTOList.add(libroCardDTO);
        Mockito.when(libroService.findAllBooksByTitoloSagaExcludingCurrent(titoloSaga, bookID)).thenReturn(libroCardDTOList);
        mockMvc.perform(get("/libri")
                .param("titoloSaga", titoloSaga)
                .param("bookId", bookID.toString())
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(libroCardDTO.getId())))
                .andExpect(jsonPath("$[0].titolo", is(libroCardDTO.getTitolo())))
                .andExpect(jsonPath("$[0].overallRating", is(libroCardDTO.getOverallRating())));
        Mockito.verify(libroService, Mockito.times(1)).findAllBooksByTitoloSagaExcludingCurrent(Mockito.anyString(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testGetAllBooksByParam_author() throws Exception {
        String author = "Pablo Neruda";
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        List<LibroCardDTO> libroCardDTOList = new ArrayList<>();
        LibroCardDTO libroCardDTO = new LibroCardDTO(1, "Prova", 4.5);
        libroCardDTOList.add(libroCardDTO);
        Mockito.when(libroService.findAllBooksByAuthor(author)).thenReturn(libroCardDTOList);
        mockMvc.perform(get("/libri")
                .param("author", author)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(libroCardDTO.getId())))
                .andExpect(jsonPath("$[0].titolo", is(libroCardDTO.getTitolo())))
                .andExpect(jsonPath("$[0].overallRating", is(libroCardDTO.getOverallRating())));
        Mockito.verify(libroService, Mockito.times(1)).findAllBooksByAuthor(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testGetAllBooksByParam_all() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        List<LibroCardDTO> libroCardDTOList = new ArrayList<>();
        LibroCardDTO libroCardDTO = new LibroCardDTO(1, "Prova", 4.5);
        libroCardDTOList.add(libroCardDTO);
        Mockito.when(libroService.findAllBooks()).thenReturn(libroCardDTOList);
        mockMvc.perform(get("/libri")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(libroCardDTO.getId())))
                .andExpect(jsonPath("$[0].titolo", is(libroCardDTO.getTitolo())))
                .andExpect(jsonPath("$[0].overallRating", is(libroCardDTO.getOverallRating())));
        Mockito.verify(libroService, Mockito.times(1)).findAllBooks();
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testVerifyTitleUniqueness() throws Exception {
        String titolo = "prova";
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Mockito.when(libroService.isTitleUnique(titolo)).thenReturn(true);
        mockMvc.perform(post("/libri/isTitleUnique")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(titolo)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
        Mockito.verify(libroService, Mockito.times(1)).isTitleUnique(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testNewBook_titoloNotUnique() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("Prova");
        libroFormDTO.setSaga(false);
        libroFormDTO.setAnnoPubblicazione(2002);
        libroFormDTO.setPagine(123);
        libroFormDTO.setSinossi("sinossi");
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Neruda");
        authorOfBook.setId(1);
        authorOfBookList.add(authorOfBook);
        libroFormDTO.setAutori(authorOfBookList);
        List<String> generi = new ArrayList<>();
        generi.add("Romanzo");
        libroFormDTO.setGeneri(generi);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(libroFormDTO);
        Mockito.when(libroService.isTitleUnique(libroFormDTO.getTitolo())).thenReturn(false);
        mockMvc.perform(post("/libri")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titolo", hasSize(1)))
                .andExpect(jsonPath("$.titolo[0]", is("titoloTaken")));
        Mockito.verify(libroService, Mockito.times(1)).isTitleUnique(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(libroService);

    }

    @Test
    public void testNewBook_yearPublishedMax() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("Prova");
        libroFormDTO.setSaga(false);
        libroFormDTO.setAnnoPubblicazione(2200);
        libroFormDTO.setPagine(123);
        libroFormDTO.setSinossi("sinossi");
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Neruda");
        authorOfBook.setId(1);
        authorOfBookList.add(authorOfBook);
        libroFormDTO.setAutori(authorOfBookList);
        List<String> generi = new ArrayList<>();
        generi.add("Romanzo");
        libroFormDTO.setGeneri(generi);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(libroFormDTO);
        Mockito.when(libroService.isTitleUnique(libroFormDTO.getTitolo())).thenReturn(true);
        mockMvc.perform(post("/libri")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.annoPubblicazione", hasSize(1)))
                .andExpect(jsonPath("$.annoPubblicazione[0]", is("max")));
        Mockito.verify(libroService, Mockito.times(1)).isTitleUnique(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testNewBook() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("Prova");
        libroFormDTO.setSaga(false);
        libroFormDTO.setAnnoPubblicazione(2002);
        libroFormDTO.setPagine(123);
        libroFormDTO.setSinossi("sinossi");
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Neruda");
        authorOfBook.setId(1);
        authorOfBookList.add(authorOfBook);
        libroFormDTO.setAutori(authorOfBookList);
        List<String> generi = new ArrayList<>();
        generi.add("Romanzo");
        libroFormDTO.setGeneri(generi);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(libroFormDTO);
        Mockito.when(libroService.isTitleUnique(libroFormDTO.getTitolo())).thenReturn(true);
        mockMvc.perform(post("/libri")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
        Mockito.verify(libroService, Mockito.times(1)).isTitleUnique(Mockito.anyString());
        Mockito.verify(libroService, Mockito.times(1)).newBook(Mockito.any(LibroFormDTO.class));
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testNewBook_forbidden() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("Prova");
        libroFormDTO.setSaga(false);
        libroFormDTO.setAnnoPubblicazione(2002);
        libroFormDTO.setPagine(123);
        libroFormDTO.setSinossi("sinossi");
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Neruda");
        authorOfBook.setId(1);
        authorOfBookList.add(authorOfBook);
        libroFormDTO.setAutori(authorOfBookList);
        List<String> generi = new ArrayList<>();
        generi.add("Romanzo");
        libroFormDTO.setGeneri(generi);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(libroFormDTO);
        mockMvc.perform(post("/libri")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        Mockito.verifyNoInteractions(libroService);
    }

    @Test
    public void testGetBook() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        LibroDTO libroDTO = new LibroDTO(bookID, "Prova", 1923, 123, "sinossi", null, null, 4.5, 4.3, 4.6, 3.9);
        Mockito.when(libroService.findBookDTOById(bookID)).thenReturn(libroDTO);
        mockMvc.perform(get("/libri/{id}", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookID)))
                .andExpect(jsonPath("$.titolo", is(libroDTO.getTitolo())));
        Mockito.verify(libroService, Mockito.times(1)).findBookDTOById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testUpdateBooksCoverPhoto() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        MockMultipartFile mf = new MockMultipartFile("copertina", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        Integer bookID = 1;
        Libro book = new Libro();
        Mockito.when(libroService.findBookById(bookID)).thenReturn(book);
        Mockito.when(libroService.updateBooksCoverPhoto(Mockito.any(MultipartFile.class), Mockito.any(Libro.class))).thenReturn("prova");
        mockMvc.perform(multipart("/libri/{id}/foto_copertina", bookID)
                .file(mf)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
                .andExpect(jsonPath("$", is("prova")));
        Mockito.verify(libroService, Mockito.times(1)).findBookById(Mockito.anyInt());
        Mockito.verify(libroService, Mockito.times(1)).updateBooksCoverPhoto(Mockito.any(MockMultipartFile.class), Mockito.any(Libro.class));
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testUpdateBooksCoverPhoto_bookNotFound() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        MockMultipartFile mf = new MockMultipartFile("copertina", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        Integer bookID = 1;
        Mockito.when(libroService.findBookById(bookID)).thenReturn(null);
        mockMvc.perform(multipart("/libri/{id}/foto_copertina", bookID)
                .file(mf)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
        Mockito.verify(libroService, Mockito.times(1)).findBookById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testUpdateBook_titoloChangedTaken() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("Prova");
        libroFormDTO.setSaga(false);
        libroFormDTO.setAnnoPubblicazione(2002);
        libroFormDTO.setPagine(123);
        libroFormDTO.setSinossi("sinossi");
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Neruda");
        authorOfBook.setId(1);
        authorOfBookList.add(authorOfBook);
        libroFormDTO.setAutori(authorOfBookList);
        List<String> generi = new ArrayList<>();
        generi.add("Romanzo");
        libroFormDTO.setGeneri(generi);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(libroFormDTO);
        Libro libro = new Libro();
        libro.setTitolo("Vecchio titolo");
        Mockito.when(libroService.findBookById(bookID)).thenReturn(libro);
        Mockito.when(libroService.isTitleUnique(libroFormDTO.getTitolo())).thenReturn(false);
        mockMvc.perform(put("/libri/{id}", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titolo", hasSize(1)))
                .andExpect(jsonPath("$.titolo[0]", is("titoloTaken")));
        Mockito.verify(libroService, Mockito.times(1)).findBookById(Mockito.anyInt());
        Mockito.verify(libroService, Mockito.times(1)).isTitleUnique(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testUpdateBook_bookNotFound() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("Prova");
        libroFormDTO.setSaga(false);
        libroFormDTO.setAnnoPubblicazione(2002);
        libroFormDTO.setPagine(123);
        libroFormDTO.setSinossi("sinossi");
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Neruda");
        authorOfBook.setId(1);
        authorOfBookList.add(authorOfBook);
        libroFormDTO.setAutori(authorOfBookList);
        List<String> generi = new ArrayList<>();
        generi.add("Romanzo");
        libroFormDTO.setGeneri(generi);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(libroFormDTO);
        Mockito.when(libroService.findBookById(bookID)).thenReturn(null);
        mockMvc.perform(put("/libri/{id}", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
        Mockito.verify(libroService, Mockito.times(1)).findBookById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testUpdateBook() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("Prova");
        libroFormDTO.setSaga(false);
        libroFormDTO.setAnnoPubblicazione(2002);
        libroFormDTO.setPagine(123);
        libroFormDTO.setSinossi("sinossi");
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Neruda");
        authorOfBook.setId(1);
        authorOfBookList.add(authorOfBook);
        libroFormDTO.setAutori(authorOfBookList);
        List<String> generi = new ArrayList<>();
        generi.add("Romanzo");
        libroFormDTO.setGeneri(generi);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(libroFormDTO);
        Libro libro = new Libro();
        libro.setTitolo("Vecchio titolo");
        Mockito.when(libroService.findBookById(bookID)).thenReturn(libro);
        Mockito.when(libroService.isTitleUnique(libroFormDTO.getTitolo())).thenReturn(true);
        mockMvc.perform(put("/libri/{id}", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
        Mockito.verify(libroService, Mockito.times(1)).findBookById(Mockito.anyInt());
        Mockito.verify(libroService, Mockito.times(1)).isTitleUnique(Mockito.anyString());
        Mockito.verify(libroService, Mockito.times(1)).updateBook(Mockito.any(LibroFormDTO.class), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testDeleteBook() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        mockMvc.perform(delete("/libri/{id}", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(libroService, Mockito.times(1)).deleteBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroService);
    }

    @Test
    public void testDeleteBook_forbidden() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        mockMvc.perform(delete("/libri/{id}", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        Mockito.verifyNoInteractions(libroService);
    }

    @Test
    public void testGetBookOverallRatiings() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        OverallRatingsForBook overallRatingsForBook = new OverallRatingsForBook(4.2, 4.5, 4.7, 3.9);
        Mockito.when(libroService.getBookOverallRatings(bookID)).thenReturn(overallRatingsForBook);
        mockMvc.perform(get("/libri/{id}/overallRatings", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.overallRating", is(overallRatingsForBook.getOverallRating())))
                .andExpect(jsonPath("$.overallOriginalityRating", is(overallRatingsForBook.getOverallOriginalityRating())))
                .andExpect(jsonPath("$.overallWritingQualityRating", is(overallRatingsForBook.getOverallWritingQualityRating())))
                .andExpect(jsonPath("$.overallPageTurnerRating", is(overallRatingsForBook.getOverallPageTurnerRating())));
        Mockito.verify(libroService, Mockito.times(1)).getBookOverallRatings(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroService);

    }
}
