package com.student.book_advisor.integrationTesting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.student.book_advisor.BookAdvisorApplication;
import com.student.book_advisor.data_persistency.model.dto.LibroCardDTO;
import com.student.book_advisor.data_persistency.model.dto.LibroDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.OverallRatingsForBook;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.LibroFormDTO;
import com.student.book_advisor.security.JwtTokenProvider;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookAdvisorApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:integrationtest.properties")
@Transactional
@Rollback
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LibroControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken = null;
    private String userToken = null;
    @Before
    public void setup() {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");
        adminToken = jwtTokenProvider.createToken("MarioRossi", 1, roles);
        List<String> userRoles = new ArrayList<>();
        userRoles.add("ROLE_USER");
        userToken = jwtTokenProvider.createToken("Tarlo", 2, userRoles);
    }

    @Test
    public void testAGetAllBooksByParam_genre() throws Exception {
        String genere = "Romanzo";
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        LibroCardDTO book1 = new LibroCardDTO(1, "Titolo", 5.0);
        LibroCardDTO book2 = new LibroCardDTO(2, "La ruota del tempo", 0.0);
        LibroCardDTO book3 = new LibroCardDTO(3, "Titolo 2", 0.0);
        AuthorOfBook author = new AuthorOfBook();
        author.setId(1);
        author.setAuthorsFullname("Pablo Neruda");
        AuthorOfBook author1 = new AuthorOfBook();
        author1.setId(2);
        author1.setAuthorsFullname("Robert Jordan");
        mockMvc.perform(get("/libri")
                .param("genere", genere)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(book1.getId())))
                .andExpect(jsonPath("$[0].titolo", is(book1.getTitolo())))
                .andExpect(jsonPath("$[0].overallRating", is(book1.getOverallRating())))
                .andExpect(jsonPath("$[0].generi", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$[0].autori", hasSize(1)))
                .andExpect(jsonPath("$[0].autori[0].id", is(author.getId())))
                .andExpect(jsonPath("$[0].autori[0].authorsFullname", is(author.getAuthorsFullname())))
                .andExpect(jsonPath("$[1].id", is(book2.getId())))
                .andExpect(jsonPath("$[1].titolo", is(book2.getTitolo())))
                .andExpect(jsonPath("$[1].overallRating", is(book2.getOverallRating())))
                .andExpect(jsonPath("$[1].generi", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$[1].autori", hasSize(1)))
                .andExpect(jsonPath("$[1].autori[0].id", is(author1.getId())))
                .andExpect(jsonPath("$[1].autori[0].authorsFullname", is(author1.getAuthorsFullname())))
                .andExpect(jsonPath("$[2].id", is(book3.getId())))
                .andExpect(jsonPath("$[2].titolo", is(book3.getTitolo())))
                .andExpect(jsonPath("$[2].overallRating", is(book3.getOverallRating())))
                .andExpect(jsonPath("$[2].generi", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$[2].autori", hasSize(1)))
                .andExpect(jsonPath("$[2].autori[0].id", is(author.getId())))
                .andExpect(jsonPath("$[2].autori[0].authorsFullname", is(author.getAuthorsFullname())));
    }
    @Test
    public void testBGetAllBooksByParam_titolo() throws Exception {
        String titolo = "Titolo";
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        LibroCardDTO book1 = new LibroCardDTO(1, "Titolo", 5.0);
        LibroCardDTO book2 = new LibroCardDTO(3, "Titolo 2", 0.0);
        AuthorOfBook author = new AuthorOfBook();
        author.setId(1);
        author.setAuthorsFullname("Pablo Neruda");
        String genere = "Romanzo";
        mockMvc.perform(get("/libri")
                .param("titolo", titolo)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(book1.getId())))
                .andExpect(jsonPath("$[0].titolo", is(book1.getTitolo())))
                .andExpect(jsonPath("$[0].overallRating", is(book1.getOverallRating())))
                .andExpect(jsonPath("$[0].generi", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$[0].autori", hasSize(1)))
                .andExpect(jsonPath("$[0].autori[0].id", is(author.getId())))
                .andExpect(jsonPath("$[0].autori[0].authorsFullname", is(author.getAuthorsFullname())))
                .andExpect(jsonPath("$[1].id", is(book2.getId())))
                .andExpect(jsonPath("$[1].titolo", is(book2.getTitolo())))
                .andExpect(jsonPath("$[1].overallRating", is(book2.getOverallRating())))
                .andExpect(jsonPath("$[1].generi", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$[1].autori", hasSize(1)))
                .andExpect(jsonPath("$[1].autori[0].id", is(author.getId())))
                .andExpect(jsonPath("$[1].autori[0].authorsFullname", is(author.getAuthorsFullname())));
    }

    @Test
    public void testCGetAllBooksByParam_titoloSagaAndBookId() throws Exception {
        String titoloSaga = "Saga";
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        LibroCardDTO book = new LibroCardDTO(3, "Titolo 2", 0.0);
        AuthorOfBook author = new AuthorOfBook();
        author.setId(1);
        author.setAuthorsFullname("Pablo Neruda");
        String genere = "Romanzo";
        mockMvc.perform(get("/libri")
                .param("titoloSaga", titoloSaga)
                .param("bookId", bookID.toString())
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(book.getId())))
                .andExpect(jsonPath("$[0].titolo", is(book.getTitolo())))
                .andExpect(jsonPath("$[0].overallRating", is(book.getOverallRating())))
                .andExpect(jsonPath("$[0].generi", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$[0].autori", hasSize(1)))
                .andExpect(jsonPath("$[0].autori[0].id", is(author.getId())))
                .andExpect(jsonPath("$[0].autori[0].authorsFullname", is(author.getAuthorsFullname())));
    }

    @Test
    public void testDGetAllBooksByParam_author() throws Exception {
        String authorName = "Pablo Neruda";
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        LibroCardDTO book1 = new LibroCardDTO(1, "Titolo", 5.0);
        LibroCardDTO book2 = new LibroCardDTO(3, "Titolo 2", 0.0);
        AuthorOfBook author = new AuthorOfBook();
        author.setId(1);
        author.setAuthorsFullname("Pablo Neruda");
        String genere = "Romanzo";
        mockMvc.perform(get("/libri")
                .param("author", authorName)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(book1.getId())))
                .andExpect(jsonPath("$[0].titolo", is(book1.getTitolo())))
                .andExpect(jsonPath("$[0].overallRating", is(book1.getOverallRating())))
                .andExpect(jsonPath("$[0].generi", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$[0].autori", hasSize(1)))
                .andExpect(jsonPath("$[0].autori[0].id", is(author.getId())))
                .andExpect(jsonPath("$[0].autori[0].authorsFullname", is(author.getAuthorsFullname())))
                .andExpect(jsonPath("$[1].id", is(book2.getId())))
                .andExpect(jsonPath("$[1].titolo", is(book2.getTitolo())))
                .andExpect(jsonPath("$[1].overallRating", is(book2.getOverallRating())))
                .andExpect(jsonPath("$[1].generi", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$[1].autori", hasSize(1)))
                .andExpect(jsonPath("$[1].autori[0].id", is(author.getId())))
                .andExpect(jsonPath("$[1].autori[0].authorsFullname", is(author.getAuthorsFullname())));
    }

    @Test
    public void testEGetAllBooksByParam_all() throws Exception {
        String genere = "Romanzo";
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        LibroCardDTO book1 = new LibroCardDTO(1, "Titolo", 5.0);
        LibroCardDTO book2 = new LibroCardDTO(2, "La ruota del tempo", 0.0);
        LibroCardDTO book3 = new LibroCardDTO(3, "Titolo 2", 0.0);
        AuthorOfBook author = new AuthorOfBook();
        author.setId(1);
        author.setAuthorsFullname("Pablo Neruda");
        AuthorOfBook author1 = new AuthorOfBook();
        author1.setId(2);
        author1.setAuthorsFullname("Robert Jordan");
        mockMvc.perform(get("/libri")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(book1.getId())))
                .andExpect(jsonPath("$[0].titolo", is(book1.getTitolo())))
                .andExpect(jsonPath("$[0].overallRating", is(book1.getOverallRating())))
                .andExpect(jsonPath("$[0].generi", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$[0].autori", hasSize(1)))
                .andExpect(jsonPath("$[0].autori[0].id", is(author.getId())))
                .andExpect(jsonPath("$[0].autori[0].authorsFullname", is(author.getAuthorsFullname())))
                .andExpect(jsonPath("$[1].id", is(book2.getId())))
                .andExpect(jsonPath("$[1].titolo", is(book2.getTitolo())))
                .andExpect(jsonPath("$[1].overallRating", is(book2.getOverallRating())))
                .andExpect(jsonPath("$[1].generi", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$[1].autori", hasSize(1)))
                .andExpect(jsonPath("$[1].autori[0].id", is(author1.getId())))
                .andExpect(jsonPath("$[1].autori[0].authorsFullname", is(author1.getAuthorsFullname())))
                .andExpect(jsonPath("$[2].id", is(book3.getId())))
                .andExpect(jsonPath("$[2].titolo", is(book3.getTitolo())))
                .andExpect(jsonPath("$[2].overallRating", is(book3.getOverallRating())))
                .andExpect(jsonPath("$[2].generi", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$[2].autori", hasSize(1)))
                .andExpect(jsonPath("$[2].autori[0].id", is(author.getId())))
                .andExpect(jsonPath("$[2].autori[0].authorsFullname", is(author.getAuthorsFullname())));
    }

    @Test
    public void testFVerifyTitleUniqueness() throws Exception {
        String titolo = "Unique";
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        mockMvc.perform(post("/libri/isTitleUnique")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(titolo)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
    }

    @Test
    public void testGGetBookOverallRatiings() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        OverallRatingsForBook overallRatingsForBook = new OverallRatingsForBook(5.0, 4.0, 4.0, 4.0);
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
    }

    @Test
    public void testHNewBook_titoloNotUnique() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("Titolo");
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titolo", hasSize(1)))
                .andExpect(jsonPath("$.titolo[0]", is("titoloTaken")));
    }

    @Test
    public void testINewBook_yearPublishedMax() throws Exception{
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
        mockMvc.perform(post("/libri")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.annoPubblicazione", hasSize(1)))
                .andExpect(jsonPath("$.annoPubblicazione[0]", is("max")));
    }

    @Test
    public void testJ0NewBook_AuthorNonExistent() throws Exception{
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
        authorOfBook.setId(4);
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
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }
    @Test
    public void testJ1NewBook_GenreNonExistent() throws Exception{
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
        generi.add("Fantasy");
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
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }

    @Test
    public void testJNewBook() throws Exception{
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
        mockMvc.perform(post("/libri")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
    }

    @Test
    public void testKNewBook_forbidden() throws Exception{
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
    }

    @Test
    public void testLGetBook() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        LibroDTO libroDTO = new LibroDTO(bookID, "Titolo", 1234, 123, "sinossi", "Saga", 1, 5.0, 4.0, 4.0, 4.0);
        AuthorOfBook author = new AuthorOfBook();
        author.setId(1);
        author.setAuthorsFullname("Pablo Neruda");
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        authorOfBookList.add(author);
        String genere = "Romanzo";
        List<String> generi = new ArrayList<>();
        generi.add(genere);
        libroDTO.setAutori(authorOfBookList);
        libroDTO.setGeneri(generi);
        libroDTO.setSaga(true);
        mockMvc.perform(get("/libri/{id}", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(bookID)))
                .andExpect(jsonPath("$.titolo", is(libroDTO.getTitolo())))
                .andExpect(jsonPath("$.overallRating", is(libroDTO.getOverallRating())))
                .andExpect(jsonPath("$.titoloSaga", is(libroDTO.getTitoloSaga())))
                .andExpect(jsonPath("$.numInSaga", is(libroDTO.getNumInSaga())))
                .andExpect(jsonPath("$.generi", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$.autori", hasSize(1)))
                .andExpect(jsonPath("$.autori[0].id", is(author.getId())))
                .andExpect(jsonPath("$.autori[0].authorsFullname", is(author.getAuthorsFullname())));
    }

    @Test
    public void testMUpdateBooksCoverPhoto() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        MockMultipartFile mf = new MockMultipartFile("copertina", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        Integer bookID = 1;
        mockMvc.perform(multipart("/libri/{id}/foto_copertina", bookID)
                .file(mf)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(jsonPath("$.img", notNullValue()));
    }

    @Test
    public void testNUpdateBooksCoverPhoto_bookNotFound() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        MockMultipartFile mf = new MockMultipartFile("copertina", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        Integer bookID = 7;
        mockMvc.perform(multipart("/libri/{id}/foto_copertina", bookID)
                .file(mf)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }

    @Test
    public void testOUpdateBook_titoloChangedTaken() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("La ruota del tempo");
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
        mockMvc.perform(put("/libri/{id}", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titolo", hasSize(1)))
                .andExpect(jsonPath("$.titolo[0]", is("titoloTaken")));
    }

    @Test
    public void testP0UpdateBook_bookNotFound() throws Exception {
        Integer bookID = 7;
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
        mockMvc.perform(put("/libri/{id}", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }

    @Test
    public void testP1UpdateBook_genreNotFound() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("Inganno");
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
        generi.add("Fantasy");
        libroFormDTO.setGeneri(generi);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(libroFormDTO);
        mockMvc.perform(put("/libri/{id}", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }

    @Test
    public void testP2UpdateBook_authorNotFound() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("Inganno");
        libroFormDTO.setSaga(false);
        libroFormDTO.setAnnoPubblicazione(2002);
        libroFormDTO.setPagine(123);
        libroFormDTO.setSinossi("sinossi");
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Neruda");
        authorOfBook.setId(4);
        authorOfBookList.add(authorOfBook);
        libroFormDTO.setAutori(authorOfBookList);
        List<String> generi = new ArrayList<>();
        generi.add("Romanzo");
        libroFormDTO.setGeneri(generi);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(libroFormDTO);
        mockMvc.perform(put("/libri/{id}", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }

    @Test
    public void testQUpdateBook() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        LibroFormDTO libroFormDTO = new LibroFormDTO();
        libroFormDTO.setTitolo("Inganno");
        libroFormDTO.setSaga(false);
        libroFormDTO.setAnnoPubblicazione(2002);
        libroFormDTO.setPagine(123);
        libroFormDTO.setSinossi("sinossi");
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Robert Jordan");
        authorOfBook.setId(2);
        authorOfBookList.add(authorOfBook);
        libroFormDTO.setAutori(authorOfBookList);
        List<String> generi = new ArrayList<>();
        generi.add("Fantascienza");
        libroFormDTO.setGeneri(generi);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(libroFormDTO);
        mockMvc.perform(put("/libri/{id}", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
    }

    @Test
    public void testRDeleteBook() throws Exception {
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
    }

    @Test
    public void testSDeleteBook_forbidden() throws Exception {
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
    }
}
