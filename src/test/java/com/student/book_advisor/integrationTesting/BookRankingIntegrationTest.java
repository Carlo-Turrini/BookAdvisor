package com.student.book_advisor.integrationTesting;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.student.book_advisor.BookAdvisorApplication;
import com.student.book_advisor.data_persistency.model.dto.BookRankingDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.BookForRankDTO;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookAdvisorApplication.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class BookRankingIntegrationTest {
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
    public void testAGetUsersBookRanking() throws Exception{
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Titolo");
        mockMvc.perform(get("/api/utenti/{id}/bookRank", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookRankingDTO.getId())))
                .andExpect(jsonPath("$[0].bookID", is(bookRankingDTO.getBookID())))
                .andExpect(jsonPath("$[0].bookRank", is(bookRankingDTO.getBookRank())))
                .andExpect(jsonPath("$[0].bookTitle", is(bookRankingDTO.getBookTitle())));
    }

    @Test
    public void testBAddBookRank_UserAuth() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        Integer userID = 2;
        BookForRankDTO bookForRankDTO = new BookForRankDTO();
        bookForRankDTO.setMyBookID(2);
        bookForRankDTO.setRank(1);
        BookRankingDTO bookRank1 = new BookRankingDTO(1, 2, 1, "Titolo");
        BookRankingDTO bookRank2 = new BookRankingDTO(3, 1, 2, "La ruota del tempo");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(bookForRankDTO);
        mockMvc.perform(post("/api/utenti/{id}/bookRank", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookRank2.getId())))
                .andExpect(jsonPath("$[0].bookID", is(bookRank2.getBookID())))
                .andExpect(jsonPath("$[0].bookRank", is(bookRank2.getBookRank())))
                .andExpect(jsonPath("$[0].bookTitle", is(bookRank2.getBookTitle())))
                .andExpect(jsonPath("$[1].id", is(bookRank1.getId())))
                .andExpect(jsonPath("$[1].bookID", is(bookRank1.getBookID())))
                .andExpect(jsonPath("$[1].bookRank", is(bookRank1.getBookRank())))
                .andExpect(jsonPath("$[1].bookTitle", is(bookRank1.getBookTitle())));
    }

    @Test
    public void testCAddBookRank_userNonExistent() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer userID = 3;
        BookForRankDTO bookForRankDTO = new BookForRankDTO();
        bookForRankDTO.setMyBookID(1);
        bookForRankDTO.setRank(1);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(bookForRankDTO);
        mockMvc.perform(post("/api/utenti/{id}/bookRank", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }

    @Test
    public void testDAddBookRank_unauthorized() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Integer userID = 2;
        BookForRankDTO bookForRankDTO = new BookForRankDTO();
        bookForRankDTO.setMyBookID(1);
        bookForRankDTO.setRank(1);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(bookForRankDTO);
        mockMvc.perform(post("/api/utenti/{id}/bookRank", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testERemoveBookRank_bookNotBelongToUser() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        Integer userID = 2;
        Integer bookRankID = 2;
        mockMvc.perform(delete("/api/utenti/{id}/bookRank/{rankID}", userID, bookRankID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }
    @Test
    public void testFRemoveBookRank_userNonExistent() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer userID = 3;
        Integer bookRankID = 2;
        mockMvc.perform(delete("/api/utenti/{id}/bookRank/{rankID}", userID, bookRankID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }

    @Test
    public void testGRemoveBookRank_userAuth() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        Integer userID = 2;
        Integer bookRankID = 3;
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Titolo");
        mockMvc.perform(delete("/api/utenti/{id}/bookRank/{rankID}", userID, bookRankID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookRankingDTO.getId())))
                .andExpect(jsonPath("$[0].bookID", is(bookRankingDTO.getBookID())))
                .andExpect(jsonPath("$[0].bookRank", is(bookRankingDTO.getBookRank())))
                .andExpect(jsonPath("$[0].bookTitle", is(bookRankingDTO.getBookTitle())));
    }

    @Test
    public void testHRemoveBookRank_adminAuth() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer userID = 2;
        Integer bookRankID = 1;
        mockMvc.perform(delete("/api/utenti/{id}/bookRank/{rankID}", userID, bookRankID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testIRemoveBookRank_unauthorized() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Integer userID = 2;
        Integer bookRankID = 2;
        mockMvc.perform(delete("/api/utenti/{id}/bookRank/{rankID}", userID, bookRankID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
