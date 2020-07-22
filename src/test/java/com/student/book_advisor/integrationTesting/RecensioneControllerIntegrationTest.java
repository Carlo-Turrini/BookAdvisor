package com.student.book_advisor.integrationTesting;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.student.book_advisor.BookAdvisorApplication;
import com.student.book_advisor.data_persistency.model.dto.RecensioneDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.RecensioneFormDTO;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookAdvisorApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:integrationtest.properties")
@Transactional
@Rollback
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RecensioneControllerIntegrationTest {
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
    public void testAGetAllReviewsByBook() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Integer bookID = 1;
        RecensioneDTO recensioneDTO = new RecensioneDTO(1, "prova", 5, Calendar.getInstance().getTime(), 2, "Tarlo", bookID, "Titolo", 4, 4, 4, false, 1L);
        mockMvc.perform(get("/libri/{id}/recensioni", bookID)
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
                .andExpect(jsonPath("$[0].userId", is(recensioneDTO.getUserId())))
                .andExpect(jsonPath("$[0].username", is(recensioneDTO.getUsername())))
                .andExpect(jsonPath("$[0].bookId", is(recensioneDTO.getBookId())))
                .andExpect(jsonPath("$[0].titolo", is(recensioneDTO.getTitolo())))
                .andExpect(jsonPath("$[0].originalityRating", is(recensioneDTO.getOriginalityRating())))
                .andExpect(jsonPath("$[0].writingQualityRating", is(recensioneDTO.getWritingQualityRating())))
                .andExpect(jsonPath("$[0].pageTurnerRating", is(recensioneDTO.getPageTurnerRating())))
                .andExpect(jsonPath("$[0].containsSpoilers", is(recensioneDTO.getContainsSpoilers())))
                .andExpect(jsonPath("$[0].numOfUsersConsideredReviewUseful", is(recensioneDTO.getNumOfUsersConsideredReviewUseful().intValue())));
    }

    @Test
    public void testBGetAllReviewsByUser() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Integer userID = 2;
        RecensioneDTO recensioneDTO = new RecensioneDTO(1, "prova", 5, Calendar.getInstance().getTime(), 2, "Tarlo", 1, "Titolo", 4, 4, 4, false, 1L);
        mockMvc.perform(get("/utenti/{id}/recensioni", userID)
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
                .andExpect(jsonPath("$[0].userId", is(recensioneDTO.getUserId())))
                .andExpect(jsonPath("$[0].username", is(recensioneDTO.getUsername())))
                .andExpect(jsonPath("$[0].bookId", is(recensioneDTO.getBookId())))
                .andExpect(jsonPath("$[0].titolo", is(recensioneDTO.getTitolo())))
                .andExpect(jsonPath("$[0].originalityRating", is(recensioneDTO.getOriginalityRating())))
                .andExpect(jsonPath("$[0].writingQualityRating", is(recensioneDTO.getWritingQualityRating())))
                .andExpect(jsonPath("$[0].pageTurnerRating", is(recensioneDTO.getPageTurnerRating())))
                .andExpect(jsonPath("$[0].containsSpoilers", is(recensioneDTO.getContainsSpoilers())))
                .andExpect(jsonPath("$[0].numOfUsersConsideredReviewUseful", is(recensioneDTO.getNumOfUsersConsideredReviewUseful().intValue())));
    }

    @Test
    public void testCAddNewReview_allFieldsRequired() throws Exception {
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
        mockMvc.perform(post("/libri/{id}/recensioni", bookID)
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
    }

    @Test
    public void testDAddNewReview() throws Exception {
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
        mockMvc.perform(post("/libri/{id}/recensioni", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
    }

    @Test
    public void testEAddNewReview_unauthorized() throws Exception {
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
        mockMvc.perform(post("/libri/{id}/recensioni", bookID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testFAddUsefulReview() throws Exception {
        Integer userID = 1;
        Integer recensioneID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        mockMvc.perform(post("/recensioni/{id}/isReviewUseful", recensioneID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(userID.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGAddUsefulReview_unauthorized() throws Exception {
        Integer userID = 2;
        Integer recensioneID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(post("/recensioni/{id}/isReviewUseful", recensioneID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(userID.toString())
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testHRemoveUsefulReview() throws Exception {
        Integer userID = 2;
        Integer recensioneID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        mockMvc.perform(delete("/recensioni/{reviewID}/isReviewUseful/{userID}", recensioneID, userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testJRemoveUsefulReview_unauthorized() throws Exception {
        Integer userID = 2;
        Integer recensioneID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(delete("/recensioni/{reviewID}/isReviewUseful/{userID}", recensioneID, userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testKDeleteReview_userAuth() throws Exception {
        Integer userID = 2;
        Integer recensioneID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        mockMvc.perform(delete("/utenti/{userId}/recensioni/{id}", userID, recensioneID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testLDeleteReview_adminAuth() throws Exception {
        Integer userID = 2;
        Integer recensioneID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        mockMvc.perform(delete("/utenti/{userId}/recensioni/{id}", userID, recensioneID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    public void testMDeleteReview_unauthorized() throws Exception {
        Integer userID = 2;
        Integer recensioneID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(delete("/utenti/{userId}/recensioni/{id}", userID, recensioneID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
