package com.student.book_advisor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.student.book_advisor.data_persistency.model.dto.AuthorCardDTO;
import com.student.book_advisor.data_persistency.model.dto.AuthorDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.AuthorOfBook;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.AuthorFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Author;
import com.student.book_advisor.data_persistency.model.entities.Authorities;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.AuthoritiesRepository;
import com.student.book_advisor.data_persistency.repositories.UsersInfoRepository;
import com.student.book_advisor.security.JwtTokenProvider;
import com.student.book_advisor.security.SecurityConfiguration;
import com.student.book_advisor.security.UserDetailsServiceImpl;
import com.student.book_advisor.security.redis.RedisUtil;
import com.student.book_advisor.services.AuthorService;
import org.hamcrest.Matchers;
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
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = AuthorController.class)
@ContextConfiguration(classes = {SecurityConfiguration.class, JwtTokenProvider.class, RedisUtil.class, UserDetailsServiceImpl.class, AuthorController.class})
public class AuthorControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private UsersInfoRepository usersInfoRepository;

    @MockBean
    private AuthoritiesRepository authoritiesRepository;

    private String adminToken = null;

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
    }

    @Test
    public void testIsAuthorsFullnameUnique() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Mockito.when(authorService.isAuthorsFullnameUnique(Mockito.anyString())).thenReturn(true);
        mockMvc.perform(post("/authors/isFullnameUnique")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content("Prova")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));
        Mockito.verify(authorService, Mockito.times(1)).isAuthorsFullnameUnique(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(authorService);
    }

    @Test
    public void testAddAuthor_fullnameTaken() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        AuthorFormDTO authorFormDTO = new AuthorFormDTO();
        authorFormDTO.setAuthorsFullname("Brandon Sanderson");
        authorFormDTO.setBirthYear(1975);
        authorFormDTO.setBiography("biografia");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(authorFormDTO);
        Mockito.when(authorService.isAuthorsFullnameUnique(Mockito.anyString())).thenReturn(false);
        mockMvc.perform(post("/authors")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.authorsFullname", hasSize(1)))
                .andExpect(jsonPath("$.authorsFullname[0]", is("fullnameTaken")));
        Mockito.verify(authorService, Mockito.times(1)).isAuthorsFullnameUnique(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(authorService);

    }

    @Test
    public void testAddAuthor() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        AuthorFormDTO authorFormDTO = new AuthorFormDTO();
        authorFormDTO.setAuthorsFullname("Brandon Sanderson");
        authorFormDTO.setBirthYear(1975);
        authorFormDTO.setBiography("biografia");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(authorFormDTO);
        Mockito.when(authorService.isAuthorsFullnameUnique(Mockito.anyString())).thenReturn(true);
        mockMvc.perform(post("/authors")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.anEmptyMap()));
        Mockito.verify(authorService, Mockito.times(1)).isAuthorsFullnameUnique(Mockito.anyString());
        Mockito.verify(authorService, Mockito.times(1)).addAuthor(Mockito.any(AuthorFormDTO.class));
        Mockito.verifyNoMoreInteractions(authorService);
    }

    @Test
    public void testUpdateAuthor_authorNotPresent() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer authorID = 1;
        AuthorFormDTO authorFormDTO = new AuthorFormDTO();
        authorFormDTO.setAuthorsFullname("Brandon Sanderson");
        authorFormDTO.setBirthYear(1975);
        authorFormDTO.setBiography("biografia");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(authorFormDTO);
        Mockito.when(authorService.getAuthor(Mockito.anyInt())).thenReturn(null);
        mockMvc.perform(put("/authors/{id}", authorID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
        Mockito.verify(authorService, Mockito.times(1)).getAuthor(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorService);
    }

    @Test
    public void testUpdateAuthor_fullnameChangedAlreadyTaken() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer authorID = 1;
        AuthorFormDTO authorFormDTO = new AuthorFormDTO();
        authorFormDTO.setAuthorsFullname("Brandon Sanderson");
        authorFormDTO.setBirthYear(1975);
        authorFormDTO.setBiography("biografia");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(authorFormDTO);
        Author author = new Author();
        author.setId(authorID);
        author.setAuthorsFullname("Pablo Neruda");
        Mockito.when(authorService.getAuthor(Mockito.anyInt())).thenReturn(author);
        Mockito.when(authorService.isAuthorsFullnameUnique(authorFormDTO.getAuthorsFullname())).thenReturn(false);
        mockMvc.perform(put("/authors/{id}", authorID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.authorsFullname", hasSize(1)))
                .andExpect(jsonPath("$.authorsFullname[0]", is("fullnameTaken")));
        Mockito.verify(authorService, Mockito.times(1)).getAuthor(Mockito.anyInt());
        Mockito.verify(authorService, Mockito.times(1)).isAuthorsFullnameUnique(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(authorService);
    }

    @Test
    public void testUpdateAuthor() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer authorID = 1;
        AuthorFormDTO authorFormDTO = new AuthorFormDTO();
        authorFormDTO.setAuthorsFullname("Brandon Sanderson");
        authorFormDTO.setBirthYear(1975);
        authorFormDTO.setBiography("biografia");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(authorFormDTO);
        Author author = new Author();
        author.setId(authorID);
        author.setAuthorsFullname("Pablo Neruda");
        Mockito.when(authorService.getAuthor(Mockito.anyInt())).thenReturn(author);
        Mockito.when(authorService.isAuthorsFullnameUnique(authorFormDTO.getAuthorsFullname())).thenReturn(true);
        mockMvc.perform(put("/authors/{id}", authorID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.anEmptyMap()));
        Mockito.verify(authorService, Mockito.times(1)).getAuthor(Mockito.anyInt());
        Mockito.verify(authorService, Mockito.times(1)).isAuthorsFullnameUnique(Mockito.anyString());
        Mockito.verify(authorService, Mockito.times(1)).updateAuthor(Mockito.any(Author.class), Mockito.any(AuthorFormDTO.class));
        Mockito.verifyNoMoreInteractions(authorService);
    }

    @Test
    public void testDeleteAuthor() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer authorID = 1;
        mockMvc.perform(delete("/authors/{id}", authorID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(authorService, Mockito.times(1)).deleteAuthor(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorService);
    }

    @Test
    public void testGetAuthorsForBookForm() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        List<AuthorOfBook> authorOfBookList = new ArrayList<>();
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setAuthorsFullname("Pablo Neruda");
        authorOfBook.setId(1);
        authorOfBookList.add(authorOfBook);
        Mockito.when(authorService.getAllAuthorsOfBook()).thenReturn(authorOfBookList);
        mockMvc.perform(get("/authors/authorsForBookForm")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(authorOfBook.getId())))
                .andExpect(jsonPath("$[0].authorsFullname", is(authorOfBook.getAuthorsFullname())));
        Mockito.verify(authorService, Mockito.times(1)).getAllAuthorsOfBook();
        Mockito.verifyNoMoreInteractions(authorService);
    }

    @Test
    public void testGetAuthor() throws Exception {
        Integer authorID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        AuthorDTO authorDTO = new AuthorDTO(authorID, "Pablo Neruda", "biografia", 1975, null);
        Mockito.when(authorService.getAuthorsDTO(authorID)).thenReturn(authorDTO);
        mockMvc.perform(get("/authors/{id}", authorID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(authorDTO.getId())))
                .andExpect(jsonPath("$.authorsFullname", is(authorDTO.getAuthorsFullname())))
                .andExpect(jsonPath("$.biography", is(authorDTO.getBiography())))
                .andExpect(jsonPath("$.birthYear", is(authorDTO.getBirthYear())))
                .andExpect(jsonPath("$.deathYear", is(authorDTO.getDeathYear())));
        Mockito.verify(authorService, Mockito.times(1)).getAuthorsDTO(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorService);
    }

    @Test
    public void testGetAllAuthors() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        List<AuthorCardDTO> authorOfBookList = new ArrayList<>();
        AuthorCardDTO authorDTO = new AuthorCardDTO(1, "Pablo Neruda", 1975, null);
        authorOfBookList.add(authorDTO);
        Mockito.when(authorService.getAllAuthors()).thenReturn(authorOfBookList);
        mockMvc.perform(get("/authors")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(authorDTO.getId())))
                .andExpect(jsonPath("$[0].authorsFullname", is(authorDTO.getAuthorsFullname())))
                .andExpect(jsonPath("$[0].birthYear", is(authorDTO.getBirthYear())))
                .andExpect(jsonPath("$[0].deathYear", is(authorDTO.getDeathYear())));
        Mockito.verify(authorService, Mockito.times(1)).getAllAuthors();
        Mockito.verifyNoMoreInteractions(authorService);
    }

    @Test
    public void testUpdateAuthorsPhoto() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        MockMultipartFile mf = new MockMultipartFile("authorsPhoto", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        Integer authorID = 1;
        Mockito.when(authorService.updateAuthorsPhoto(mf, authorID)).thenReturn("prova");
        mockMvc.perform(multipart("/authors/{id}/authors_photo", authorID)
                .file(mf)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
                .andExpect(jsonPath("$", is("prova")));
        Mockito.verify(authorService, Mockito.times(1)).updateAuthorsPhoto(Mockito.any(MockMultipartFile.class), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorService);
    }

    @Test
    public void testGetAuthorOfBookFromFullname() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        AuthorOfBook authorOfBook = new AuthorOfBook();
        authorOfBook.setId(1);
        authorOfBook.setAuthorsFullname("Pablo Neruda");
        Mockito.when(authorService.getAuthorOfBookByFullname(authorOfBook.getAuthorsFullname())).thenReturn(authorOfBook);
        mockMvc.perform(get("/authors/byName/{fullname}", authorOfBook.getAuthorsFullname())
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(authorOfBook.getId())))
                .andExpect(jsonPath("$.authorsFullname", is(authorOfBook.getAuthorsFullname())));
        Mockito.verify(authorService, Mockito.times(1)).getAuthorOfBookByFullname(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(authorService);
    }
}
