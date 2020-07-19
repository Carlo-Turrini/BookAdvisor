package com.student.book_advisor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.student.book_advisor.data_persistency.model.dto.BookRankingDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.BookForRankDTO;
import com.student.book_advisor.data_persistency.model.entities.Authorities;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.AuthoritiesRepository;
import com.student.book_advisor.data_persistency.repositories.UsersInfoRepository;
import com.student.book_advisor.security.JwtTokenProvider;
import com.student.book_advisor.security.SecurityConfiguration;
import com.student.book_advisor.security.UserDetailsServiceImpl;
import com.student.book_advisor.security.redis.RedisUtil;
import com.student.book_advisor.services.BookRankingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BookRankingController.class)
@ContextConfiguration(classes = {SecurityConfiguration.class, JwtTokenProvider.class, RedisUtil.class, UserDetailsServiceImpl.class, BookRankingController.class})
public class BookRankingControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private BookRankingService bookRankingService;

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
    public void testGetUsersBookRanking() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        List<BookRankingDTO> bookRankingDTOList = new ArrayList<>();
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Prova");
        bookRankingDTOList.add(bookRankingDTO);
        Mockito.when(bookRankingService.findUsersBookRank(Mockito.anyInt())).thenReturn(bookRankingDTOList);
        mockMvc.perform(get("/utenti/{id}/bookRank", 2)
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
        Mockito.verify(bookRankingService, Mockito.times(1)).findUsersBookRank(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingService);
    }

    @Test
    public void testAddBookRank_UserAuth() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        Integer userID = 2;
        BookForRankDTO bookForRankDTO = new BookForRankDTO();
        bookForRankDTO.setMyBookID(1);
        bookForRankDTO.setRank(1);
        List<BookRankingDTO> bookRankingDTOList = new ArrayList<>();
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Prova");
        bookRankingDTOList.add(bookRankingDTO);
        Mockito.when(bookRankingService.addBookToBookRank(userID, bookForRankDTO.getMyBookID(), bookForRankDTO.getRank())).thenReturn(bookRankingDTOList);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(bookForRankDTO);
        mockMvc.perform(post("/utenti/{id}/bookRank", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookRankingDTO.getId())))
                .andExpect(jsonPath("$[0].bookID", is(bookRankingDTO.getBookID())))
                .andExpect(jsonPath("$[0].bookRank", is(bookRankingDTO.getBookRank())))
                .andExpect(jsonPath("$[0].bookTitle", is(bookRankingDTO.getBookTitle())));
        Mockito.verify(bookRankingService, Mockito.times(1)).addBookToBookRank(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingService);
    }

    @Test
    public void testAddBookRank_adminAuth() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer userID = 2;
        BookForRankDTO bookForRankDTO = new BookForRankDTO();
        bookForRankDTO.setMyBookID(1);
        bookForRankDTO.setRank(1);
        List<BookRankingDTO> bookRankingDTOList = new ArrayList<>();
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Prova");
        bookRankingDTOList.add(bookRankingDTO);
        Mockito.when(bookRankingService.addBookToBookRank(userID, bookForRankDTO.getMyBookID(), bookForRankDTO.getRank())).thenReturn(bookRankingDTOList);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(bookForRankDTO);
        mockMvc.perform(post("/utenti/{id}/bookRank", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookRankingDTO.getId())))
                .andExpect(jsonPath("$[0].bookID", is(bookRankingDTO.getBookID())))
                .andExpect(jsonPath("$[0].bookRank", is(bookRankingDTO.getBookRank())))
                .andExpect(jsonPath("$[0].bookTitle", is(bookRankingDTO.getBookTitle())));
        Mockito.verify(bookRankingService, Mockito.times(1)).addBookToBookRank(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingService);
    }

    @Test
    public void testAddBookRank_unauthorized() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Integer userID = 2;
        BookForRankDTO bookForRankDTO = new BookForRankDTO();
        bookForRankDTO.setMyBookID(1);
        bookForRankDTO.setRank(1);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(bookForRankDTO);
        mockMvc.perform(post("/utenti/{id}/bookRank", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        Mockito.verifyNoInteractions(bookRankingService);
    }

    @Test
    public void testRemoveBookRank_userAuth() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        Integer userID = 2;
        Integer bookRankID = 2;
        List<BookRankingDTO> bookRankingDTOList = new ArrayList<>();
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Prova");
        bookRankingDTOList.add(bookRankingDTO);
        Mockito.when(bookRankingService.removeBookFromBookRank(userID, bookRankID)).thenReturn(bookRankingDTOList);
        mockMvc.perform(delete("/utenti/{id}/bookRank/{rankID}", userID, bookRankID)
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
        Mockito.verify(bookRankingService, Mockito.times(1)).removeBookFromBookRank(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingService);
    }

    @Test
    public void testRemoveBookRank_adminAuth() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer userID = 2;
        Integer bookRankID = 2;
        List<BookRankingDTO> bookRankingDTOList = new ArrayList<>();
        BookRankingDTO bookRankingDTO = new BookRankingDTO(1, 1, 1, "Prova");
        bookRankingDTOList.add(bookRankingDTO);
        Mockito.when(bookRankingService.removeBookFromBookRank(userID, bookRankID)).thenReturn(bookRankingDTOList);
        mockMvc.perform(delete("/utenti/{id}/bookRank/{rankID}", userID, bookRankID)
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
        Mockito.verify(bookRankingService, Mockito.times(1)).removeBookFromBookRank(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(bookRankingService);
    }

    @Test
    public void testRemoveBookRank_unauthorized() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Integer userID = 2;
        Integer bookRankID = 2;
        mockMvc.perform(delete("/utenti/{id}/bookRank/{rankID}", userID, bookRankID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        Mockito.verifyNoInteractions(bookRankingService);
    }
}
