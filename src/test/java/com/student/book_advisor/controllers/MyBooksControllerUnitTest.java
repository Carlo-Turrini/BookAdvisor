package com.student.book_advisor.controllers;

import com.student.book_advisor.data_persistency.model.dto.MyBooksDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.MyBooksReadDTO;
import com.student.book_advisor.data_persistency.model.entities.Authorities;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.AuthoritiesRepository;
import com.student.book_advisor.data_persistency.repositories.UsersInfoRepository;
import com.student.book_advisor.enums.BookShelf;
import com.student.book_advisor.security.JwtTokenProvider;
import com.student.book_advisor.security.SecurityConfiguration;
import com.student.book_advisor.security.UserDetailsServiceImpl;
import com.student.book_advisor.security.redis.RedisUtil;
import com.student.book_advisor.services.MyBooksService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = MyBooksController.class)
@ContextConfiguration(classes = {SecurityConfiguration.class, JwtTokenProvider.class, RedisUtil.class, UserDetailsServiceImpl.class, MyBooksController.class})
public class MyBooksControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private MyBooksService myBooksService;

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
    public void testDeleteBookFromMyBooks_userAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        Mockito.when(myBooksService.deleteFromShelf(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        mockMvc.perform(delete("/utenti/{id}/myBooks/{myBookID}", userID, 1)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));
        Mockito.verify(myBooksService, Mockito.times(1)).deleteFromShelf(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(myBooksService);
    }

    @Test
    public void testDeleteBookFromMyBooks_adminAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Mockito.when(myBooksService.deleteFromShelf(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        mockMvc.perform(delete("/utenti/{id}/myBooks/{myBookID}", userID, 1)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));
        Mockito.verify(myBooksService, Mockito.times(1)).deleteFromShelf(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(myBooksService);
    }

    @Test
    public void testDeleteBookFromMyBooks_unauthorized() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(delete("/utenti/{id}/myBooks/{myBookID}", userID, 1)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        Mockito.verifyNoInteractions(myBooksService);
    }

    @Test
    public void testUpdateBookFromMyBooks_userAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        Mockito.when(myBooksService.updateShelf(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(BookShelf.class))).thenReturn(true);
        mockMvc.perform(put("/utenti/{id}/myBooks/{bookID}", userID, 1)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(BookShelf.reading.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));
        Mockito.verify(myBooksService, Mockito.times(1)).updateShelf(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(BookShelf.class));
        Mockito.verifyNoMoreInteractions(myBooksService);
    }

    @Test
    public void testUpdateBookFromMyBooks_adminAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Mockito.when(myBooksService.updateShelf(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(BookShelf.class))).thenReturn(true);
        mockMvc.perform(put("/utenti/{id}/myBooks/{bookID}", userID, 1)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(BookShelf.reading.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));
        Mockito.verify(myBooksService, Mockito.times(1)).updateShelf(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(BookShelf.class));
        Mockito.verifyNoMoreInteractions(myBooksService);
    }

    @Test
    public void testUpdateBookFromMyBooks_unauthorized() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(put("/utenti/{id}/myBooks/{bookID}", userID, 1)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(BookShelf.read.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        Mockito.verifyNoInteractions(myBooksService);
    }

    @Test
    public void testGetAllMyBooksReadNotInRank_userAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        List<MyBooksReadDTO> myBooksReadDTOList = new ArrayList<>();
        MyBooksReadDTO myBooksReadDTO = new MyBooksReadDTO(1, "Prova");
        myBooksReadDTOList.add(myBooksReadDTO);
        Mockito.when(myBooksService.findAllMyBooksRead(Mockito.anyInt())).thenReturn(myBooksReadDTOList);
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
        Mockito.verify(myBooksService, Mockito.times(1)).findAllMyBooksRead(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(myBooksService);
    }

    @Test
    public void testGetAllMyBooksReadNotInRank_adminAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        List<MyBooksReadDTO> myBooksReadDTOList = new ArrayList<>();
        MyBooksReadDTO myBooksReadDTO = new MyBooksReadDTO(1, "Prova");
        myBooksReadDTOList.add(myBooksReadDTO);
        Mockito.when(myBooksService.findAllMyBooksRead(Mockito.anyInt())).thenReturn(myBooksReadDTOList);
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
        Mockito.verify(myBooksService, Mockito.times(1)).findAllMyBooksRead(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(myBooksService);
    }

    @Test
    public void testGetAllMyBooksReadNotInRank_unauthorized() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(get("/utenti/{id}/myBooks/booksReadNotInRank", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        Mockito.verifyNoInteractions(myBooksService);
    }

    @Test
    public void testGetAllMyBooks() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        List<MyBooksDTO> myBooksDTOList = new ArrayList<>();
        MyBooksDTO myBooksDTO = new MyBooksDTO(1, 1, "Prova", BookShelf.read, userID, 4.5);
        myBooksDTOList.add(myBooksDTO);
        Mockito.when(myBooksService.findAllMyBooks(userID)).thenReturn(myBooksDTOList);
        mockMvc.perform(get("/utenti/{id}/myBooks", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(myBooksDTO.getId())))
                .andExpect(jsonPath("$[0].bookID", is(myBooksDTO.getBookID())))
                .andExpect(jsonPath("$[0].titolo", is(myBooksDTO.getTitolo())))
                .andExpect(jsonPath("$[0].shelf", is(myBooksDTO.getShelf().toString())))
                .andExpect(jsonPath("$[0].userID", is(myBooksDTO.getUserID())))
                .andExpect(jsonPath("$[0].overallRating", is(myBooksDTO.getOverallRating())));
        Mockito.verify(myBooksService, Mockito.times(1)).findAllMyBooks(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(myBooksService);
    }

    @Test
    public void testAddBookToShelf_userAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        BookShelf shelf = BookShelf.read;
        Mockito.when(myBooksService.addToShelf(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(BookShelf.class))).thenReturn(shelf.toString());
        mockMvc.perform(post("/utenti/{id}/myBooks/{bookID}", userID, 1)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(shelf.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
                .andExpect(jsonPath("$.shelf", is(shelf.toString())));
        Mockito.verify(myBooksService, Mockito.times(1)).addToShelf(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(BookShelf.class));
        Mockito.verifyNoMoreInteractions(myBooksService);
    }

    @Test
    public void testAddBookToShelf_adminAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        BookShelf shelf = BookShelf.read;
        Mockito.when(myBooksService.addToShelf(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(BookShelf.class))).thenReturn(shelf.toString());
        mockMvc.perform(post("/utenti/{id}/myBooks/{bookID}", userID, 1)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(shelf.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
                .andExpect(jsonPath("$.shelf", is(shelf.toString())));
        Mockito.verify(myBooksService, Mockito.times(1)).addToShelf(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(BookShelf.class));
        Mockito.verifyNoMoreInteractions(myBooksService);
    }

    @Test
    public void testAddBookToShelf_unauthorized() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        BookShelf shelf = BookShelf.read;
        mockMvc.perform(post("/utenti/{id}/myBooks/{bookID}", userID, 1)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(shelf.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        Mockito.verifyNoInteractions(myBooksService);
    }
}
