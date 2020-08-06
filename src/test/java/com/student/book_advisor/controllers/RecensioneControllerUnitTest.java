package com.student.book_advisor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.student.book_advisor.data_persistency.model.dto.RecensioneDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.RecensioneFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Authorities;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.AuthoritiesRepository;
import com.student.book_advisor.data_persistency.repositories.UsersInfoRepository;
import com.student.book_advisor.security.JwtTokenProvider;
import com.student.book_advisor.security.SecurityConfiguration;
import com.student.book_advisor.security.UserDetailsServiceImpl;
import com.student.book_advisor.security.redis.RedisUtil;
import com.student.book_advisor.services.RecensioneService;
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
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = RecensioneController.class)
@ContextConfiguration(classes = {SecurityConfiguration.class, JwtTokenProvider.class, RedisUtil.class, UserDetailsServiceImpl.class, RecensioneController.class})
public class RecensioneControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private RecensioneService recensioneService;

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
    public void testGetAllReviewsByBook() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Integer bookID = 1;
        List<RecensioneDTO> recensioneDTOList = new ArrayList<>();
        RecensioneDTO recensioneDTO = new RecensioneDTO(1, "prova", 4, Calendar.getInstance().getTime(), 1, "MarioRossi", bookID, "Prova", 4, 5, 3, false, 12L);
        recensioneDTOList.add(recensioneDTO);
        Mockito.when(recensioneService.getAllReviewsByBook(bookID)).thenReturn(recensioneDTOList);
        mockMvc.perform(get("/api/libri/{id}/recensioni", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(recensioneDTO.getId())))
                .andExpect(jsonPath("$[0].testo", is(recensioneDTO.getTesto())))
                .andExpect(jsonPath("$[0].rating", is(recensioneDTO.getRating())))
                .andExpect(jsonPath("$[0].timestamp", is(recensioneDTO.getTimestamp())))
                .andExpect(jsonPath("$[0].userId", is(recensioneDTO.getUserId())))
                .andExpect(jsonPath("$[0].username", is(recensioneDTO.getUsername())))
                .andExpect(jsonPath("$[0].bookId", is(recensioneDTO.getBookId())))
                .andExpect(jsonPath("$[0].titolo", is(recensioneDTO.getTitolo())))
                .andExpect(jsonPath("$[0].originalityRating", is(recensioneDTO.getOriginalityRating())))
                .andExpect(jsonPath("$[0].writingQualityRating", is(recensioneDTO.getWritingQualityRating())))
                .andExpect(jsonPath("$[0].pageTurnerRating", is(recensioneDTO.getPageTurnerRating())))
                .andExpect(jsonPath("$[0].containsSpoilers", is(recensioneDTO.getContainsSpoilers())))
                .andExpect(jsonPath("$[0].numOfUsersConsideredReviewUseful", is(recensioneDTO.getNumOfUsersConsideredReviewUseful().intValue())));
        Mockito.verify(recensioneService, Mockito.times(1)).getAllReviewsByBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneService);
    }

    @Test
    public void testGetAllReviewsByUser() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Integer userID = 1;
        List<RecensioneDTO> recensioneDTOList = new ArrayList<>();
        RecensioneDTO recensioneDTO = new RecensioneDTO(1, "prova", 4, Calendar.getInstance().getTime(), userID, "MarioRossi", 1, "Prova", 4, 5, 3, false, 12L);
        recensioneDTOList.add(recensioneDTO);
        Mockito.when(recensioneService.getAllReveiewsByUser(userID)).thenReturn(recensioneDTOList);
        mockMvc.perform(get("/api/utenti/{id}/recensioni", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(recensioneDTO.getId())))
                .andExpect(jsonPath("$[0].testo", is(recensioneDTO.getTesto())))
                .andExpect(jsonPath("$[0].rating", is(recensioneDTO.getRating())))
                .andExpect(jsonPath("$[0].timestamp", is(recensioneDTO.getTimestamp())))
                .andExpect(jsonPath("$[0].userId", is(recensioneDTO.getUserId())))
                .andExpect(jsonPath("$[0].username", is(recensioneDTO.getUsername())))
                .andExpect(jsonPath("$[0].bookId", is(recensioneDTO.getBookId())))
                .andExpect(jsonPath("$[0].titolo", is(recensioneDTO.getTitolo())))
                .andExpect(jsonPath("$[0].originalityRating", is(recensioneDTO.getOriginalityRating())))
                .andExpect(jsonPath("$[0].writingQualityRating", is(recensioneDTO.getWritingQualityRating())))
                .andExpect(jsonPath("$[0].pageTurnerRating", is(recensioneDTO.getPageTurnerRating())))
                .andExpect(jsonPath("$[0].containsSpoilers", is(recensioneDTO.getContainsSpoilers())))
                .andExpect(jsonPath("$[0].numOfUsersConsideredReviewUseful", is(recensioneDTO.getNumOfUsersConsideredReviewUseful().intValue())));
        Mockito.verify(recensioneService, Mockito.times(1)).getAllReveiewsByUser(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneService);
    }

    @Test
    public void testAddNewReview_allFieldsRequired() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        RecensioneFormDTO recensioneFormDTO = new RecensioneFormDTO();
        recensioneFormDTO.setContainsSpoilers(false);
        recensioneFormDTO.setWritingQualityRating(null);
        recensioneFormDTO.setOriginalityRating(null);
        recensioneFormDTO.setPageTurnerRating(null);
        recensioneFormDTO.setRating(null);
        recensioneFormDTO.setTesto("");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(recensioneFormDTO);
        mockMvc.perform(post("/api/libri/{id}/recensioni", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalityRating", hasSize(1)))
                .andExpect(jsonPath("$.originalityRating[0]", is("required")))
                .andExpect(jsonPath("$.writingQualityRating", hasSize(1)))
                .andExpect(jsonPath("$.writingQualityRating[0]", is("required")))
                .andExpect(jsonPath("$.pageTurnerRating", hasSize(1)))
                .andExpect(jsonPath("$.pageTurnerRating[0]", is("required")))
                .andExpect(jsonPath("$.rating", hasSize(1)))
                .andExpect(jsonPath("$.rating[0]", is("required")))
                .andExpect(jsonPath("$.testo", hasSize(2)))
                .andExpect(jsonPath("$.testo", containsInAnyOrder("minlength","required")));
        Mockito.verifyNoInteractions(recensioneService);
    }

    @Test
    public void testAddNewReview() throws Exception {
        Integer bookID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        RecensioneFormDTO recensioneFormDTO = new RecensioneFormDTO();
        recensioneFormDTO.setContainsSpoilers(false);
        recensioneFormDTO.setWritingQualityRating(4);
        recensioneFormDTO.setOriginalityRating(5);
        recensioneFormDTO.setPageTurnerRating(3);
        recensioneFormDTO.setRating(5);
        recensioneFormDTO.setTesto("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(recensioneFormDTO);
        mockMvc.perform(post("/api/libri/{id}/recensioni", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
        Mockito.verify(recensioneService, Mockito.times(1)).addNewReview(Mockito.any(RecensioneFormDTO.class), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneService);
    }

    @Test
    public void testAddNewReview_unauthorized() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Integer bookID = 1;
        RecensioneFormDTO recensioneFormDTO = new RecensioneFormDTO();
        recensioneFormDTO.setContainsSpoilers(false);
        recensioneFormDTO.setWritingQualityRating(null);
        recensioneFormDTO.setOriginalityRating(null);
        recensioneFormDTO.setPageTurnerRating(null);
        recensioneFormDTO.setRating(null);
        recensioneFormDTO.setTesto("");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(recensioneFormDTO);
        mockMvc.perform(post("/api/libri/{id}/recensioni", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        Mockito.verifyNoInteractions(recensioneService);
    }

    @Test
    public void testDeleteReview_userAuth() throws Exception {
        Integer userID = 2;
        Integer recensioneID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        mockMvc.perform(delete("/api/utenti/{userId}/recensioni/{id}", userID, recensioneID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(recensioneService, Mockito.times(1)).deleteReview(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneService);
    }

    @Test
    public void testDeleteReview_adminAuth() throws Exception {
        Integer userID = 2;
        Integer recensioneID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        mockMvc.perform(delete("/api/utenti/{userId}/recensioni/{id}", userID, recensioneID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(recensioneService, Mockito.times(1)).deleteReview(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneService);    }

    @Test
    public void testDeleteReview_unauthorized() throws Exception {
        Integer userID = 2;
        Integer recensioneID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(delete("/api/utenti/{userId}/recensioni/{id}", userID, recensioneID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        Mockito.verifyNoInteractions(recensioneService);
    }

    @Test
    public void testAddUsefulReview() throws Exception {
        Integer userID = 2;
        Integer recensioneID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        mockMvc.perform(post("/api/recensioni/{id}/isReviewUseful", recensioneID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(userID.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(recensioneService, Mockito.times(1)).addUsefulReview(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneService);
    }

    @Test
    public void testAddUsefulReview_unauthorized() throws Exception {
        Integer userID = 2;
        Integer recensioneID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(post("/api/recensioni/{id}/isReviewUseful", recensioneID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(userID.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        Mockito.verifyNoInteractions(recensioneService);
    }

    @Test
    public void testRemoveUsefulReview() throws Exception {
        Integer userID = 2;
        Integer recensioneID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        mockMvc.perform(delete("/api/recensioni/{reviewID}/isReviewUseful/{userID}", recensioneID, userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(recensioneService, Mockito.times(1)).removeUsefulReview(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneService);
    }

    @Test
    public void testRemoveUsefulReview_unauthorized() throws Exception {
        Integer userID = 2;
        Integer recensioneID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(delete("/api/recensioni/{reviewID}/isReviewUseful/{userID}", recensioneID, userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        Mockito.verifyNoInteractions(recensioneService);
    }
}
