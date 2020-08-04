package com.student.book_advisor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.GenreFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Authorities;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.AuthoritiesRepository;
import com.student.book_advisor.data_persistency.repositories.UsersInfoRepository;
import com.student.book_advisor.security.JwtTokenProvider;
import com.student.book_advisor.security.SecurityConfiguration;
import com.student.book_advisor.security.UserDetailsServiceImpl;
import com.student.book_advisor.security.redis.RedisUtil;
import com.student.book_advisor.services.GenreService;
import org.hamcrest.Matchers;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = GenreController.class)
@ContextConfiguration(classes = {SecurityConfiguration.class, JwtTokenProvider.class, RedisUtil.class, UserDetailsServiceImpl.class, GenreController.class})
public class GenreControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private GenreService genreService;

    @MockBean
    private UsersInfoRepository usersInfoRepository;

    @MockBean
    private AuthoritiesRepository authoritiesRepository;

    private String adminToken = null;

    @Before
    public void setup() {
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setId(1);
        usersInfo.setUsername("adminUser");
        usersInfo.setPassword("password");

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");

        adminToken = jwtTokenProvider.createToken(usersInfo.getUsername(), usersInfo.getId(), roles);
        Mockito.when(usersInfoRepository.findByUsername(usersInfo.getUsername())).thenReturn(usersInfo);
        List<Authorities> authoritiesList = new ArrayList<>();
        Authorities authUser  = new Authorities();
        authUser.setAuthority("USER");
        Authorities authAdmin = new Authorities();
        authAdmin.setAuthority("ADMIN");
        authoritiesList.add(authUser);
        authoritiesList.add(authAdmin);
        Mockito.when(authoritiesRepository.findAllByUserID(usersInfo.getId())).thenReturn(authoritiesList);
    }
    @Test
    public void testIsGenreUnique() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        given(genreService.isGenreUnique("Romanzo")).willReturn(true);
        String genre = "Romanzo";
        mockMvc.perform(post("/genres/isGenreUnique")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(genre)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));
    }

    @Test
    public void testGetAllGenres() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        List<String> genres = new ArrayList<>();
        genres.add("Romanzo");
        //given(genreService.getAllGenres()).willReturn(genres);
        Mockito.when(genreService.getAllGenres()).thenReturn(genres);
        mockMvc.perform(get("/genres")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]", is("Romanzo")));
    }

    @Test
    public void testAddGenre_genreNotUnique() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        GenreFormDTO genreFormDTO = new GenreFormDTO();
        genreFormDTO.setGenre("Romanzo");
        Mockito.when(genreService.isGenreUnique(genreFormDTO.getGenre())).thenReturn(false);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(genreFormDTO);
        mockMvc.perform(post("/genres")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.genre", hasSize(1)))
                .andExpect(jsonPath("$.genre[0]", is("genreTaken")));
        Mockito.verify(genreService, times(1)).isGenreUnique(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(genreService);
    }

    @Test
    public void testAddGenre() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        GenreFormDTO genreFormDTO = new GenreFormDTO();
        genreFormDTO.setGenre("Romanzo");
        Mockito.when(genreService.isGenreUnique(genreFormDTO.getGenre())).thenReturn(true);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(genreFormDTO);
        mockMvc.perform(post("/genres")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.anEmptyMap()));
        Mockito.verify(genreService, times(1)).isGenreUnique(Mockito.anyString());
        Mockito.verify(genreService, times(1)).addGenre(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(genreService);

    }
}