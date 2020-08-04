package com.student.book_advisor.integrationTesting;

import com.student.book_advisor.BookAdvisorApplication;
import com.student.book_advisor.data_persistency.model.dto.MyBooksDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.MyBooksReadDTO;
import com.student.book_advisor.enums.BookShelf;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookAdvisorApplication.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class MyBooksControllerIntegrationTest {
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
    public void testAGetAllMyBooksReadNotInRank_userAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        MyBooksReadDTO myBooksReadDTO = new MyBooksReadDTO(2, "La ruota del tempo");
        mockMvc.perform(get("/utenti/{id}/myBooks/booksReadNotInRank", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].myBooksID", is(myBooksReadDTO.getMyBooksID())))
                .andExpect(jsonPath("$[0].title", is(myBooksReadDTO.getTitle())));
    }

    @Test
    public void testBGetAllMyBooksReadNotInRank_adminAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        MyBooksReadDTO myBooksReadDTO = new MyBooksReadDTO(2, "La ruota del tempo");
        mockMvc.perform(get("/utenti/{id}/myBooks/booksReadNotInRank", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].myBooksID", is(myBooksReadDTO.getMyBooksID())))
                .andExpect(jsonPath("$[0].title", is(myBooksReadDTO.getTitle())));
    }

    @Test
    public void testCGetAllMyBooksReadNotInRank_unauthorized() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(get("/utenti/{id}/myBooks/booksReadNotInRank", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDGetAllMyBooks() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        MyBooksDTO myBooks1 = new MyBooksDTO(1, 1, "Titolo", BookShelf.read, userID, 5.0);
        MyBooksDTO myBooks2 = new MyBooksDTO(2, 2, "La ruota del tempo", BookShelf.read, userID, 0.0);
        String genere = "Romanzo";
        List<String> generi = new ArrayList<>();
        generi.add(genere);
        List<AuthorOfBook> authorOfBookList1 = new ArrayList<>();
        AuthorOfBook author1 = new AuthorOfBook();
        author1.setId(1);
        author1.setAuthorsFullname("Pablo Neruda");
        authorOfBookList1.add(author1);
        myBooks1.setAuthors(authorOfBookList1);
        myBooks1.setGenres(generi);
        List<AuthorOfBook> authorOfBookList2 = new ArrayList<>();
        AuthorOfBook author2 = new AuthorOfBook();
        author2.setId(2);
        author2.setAuthorsFullname("Robert Jordan");
        authorOfBookList2.add(author2);
        myBooks2.setAuthors(authorOfBookList2);
        myBooks2.setGenres(generi);
        mockMvc.perform(get("/utenti/{id}/myBooks", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(myBooks1.getId())))
                .andExpect(jsonPath("$[0].bookID", is(myBooks1.getBookID())))
                .andExpect(jsonPath("$[0].titolo", is(myBooks1.getTitolo())))
                .andExpect(jsonPath("$[0].shelf", is(myBooks1.getShelf().toString())))
                .andExpect(jsonPath("$[0].userID", is(myBooks1.getUserID())))
                .andExpect(jsonPath("$[0].overallRating", is(myBooks1.getOverallRating())))
                .andExpect(jsonPath("$[0].genres", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$[0].authors", hasSize(1)))
                .andExpect(jsonPath("$[0].authors[0].id", is(author1.getId())))
                .andExpect(jsonPath("$[0].authors[0].authorsFullname", is(author1.getAuthorsFullname())))
                .andExpect(jsonPath("$[1].id", is(myBooks2.getId())))
                .andExpect(jsonPath("$[1].bookID", is(myBooks2.getBookID())))
                .andExpect(jsonPath("$[1].titolo", is(myBooks2.getTitolo())))
                .andExpect(jsonPath("$[1].shelf", is(myBooks2.getShelf().toString())))
                .andExpect(jsonPath("$[1].userID", is(myBooks2.getUserID())))
                .andExpect(jsonPath("$[1].overallRating", is(myBooks2.getOverallRating())))
                .andExpect(jsonPath("$[1].genres", containsInAnyOrder(genere)))
                .andExpect(jsonPath("$[1].authors", hasSize(1)))
                .andExpect(jsonPath("$[1].authors[0].id", is(author2.getId())))
                .andExpect(jsonPath("$[1].authors[0].authorsFullname", is(author2.getAuthorsFullname())));
    }

    @Test
    public void testEAddBookToShelf_userAuth() throws Exception {
        Integer userID = 2;
        Integer bookID = 3;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        BookShelf shelf = BookShelf.reading;
        mockMvc.perform(post("/utenti/{id}/myBooks/{bookID}", userID, bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(shelf.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(jsonPath("$.shelf", is(shelf.toString())));
    }

    @Test
    public void testFAddBookToShelf_bookNonExistent() throws Exception {
        Integer userID = 2;
        Integer bookID = 6;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        BookShelf shelf = BookShelf.reading;
        mockMvc.perform(post("/utenti/{id}/myBooks/{bookID}", userID, bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(shelf.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }

    @Test
    public void testGAddBookToShelf_bookAlreadyPartOfMyBooks() throws Exception {
        Integer userID = 2;
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        BookShelf shelf = BookShelf.reading;
        mockMvc.perform(post("/utenti/{id}/myBooks/{bookID}", userID, bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(shelf.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }

    @Test
    public void testGAddBookToShelf_userNonExistent() throws Exception {
        Integer userID = 3;
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        BookShelf shelf = BookShelf.reading;
        mockMvc.perform(post("/utenti/{id}/myBooks/{bookID}", userID, bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(shelf.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }

    @Test
    public void testHAddBookToShelf_unauthorized() throws Exception {
        Integer userID = 2;
        Integer bookID = 3;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        BookShelf shelf = BookShelf.read;
        mockMvc.perform(post("/utenti/{id}/myBooks/{bookID}", userID, bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(shelf.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testIUpdateBookFromMyBooks_userAuth() throws Exception {
        Integer userID = 2;
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        mockMvc.perform(put("/utenti/{id}/myBooks/{bookID}", userID, bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(BookShelf.reading.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));
    }

    @Test
    public void testJUpdateBookFromMyBooks_bookNotPartOfMyBooks() throws Exception {
        Integer userID = 1;
        Integer bookID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        mockMvc.perform(put("/utenti/{id}/myBooks/{bookID}", userID, bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(BookShelf.reading.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }

    @Test
    public void testKUpdateBookFromMyBooks_adminAuth() throws Exception {
        Integer userID = 2;
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        mockMvc.perform(put("/utenti/{id}/myBooks/{bookID}", userID, bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(BookShelf.read.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(false)));
    }

    @Test
    public void testLUpdateBookFromMyBooks_unauthorized() throws Exception {
        Integer userID = 2;
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(put("/utenti/{id}/myBooks/{bookID}", userID, bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(BookShelf.read.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testMDeleteBookFromMyBooks_userAuth() throws Exception {
        Integer userID = 2;
        Integer bookID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        mockMvc.perform(delete("/utenti/{id}/myBooks/{myBookID}", userID, bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(false)));
    }

    @Test
    public void testNDeleteBookFromMyBooks_adminAuth() throws Exception {
        Integer userID = 2;
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        mockMvc.perform(delete("/utenti/{id}/myBooks/{myBookID}", userID, bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(false)));
    }

    @Test
    public void testODeleteBookFromMyBooks_unauthorized() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(delete("/utenti/{id}/myBooks/{myBookID}", userID, 1)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
