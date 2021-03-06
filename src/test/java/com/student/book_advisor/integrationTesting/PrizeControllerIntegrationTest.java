package com.student.book_advisor.integrationTesting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.student.book_advisor.BookAdvisorApplication;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.PrizeFormDTO;
import com.student.book_advisor.security.JwtTokenProvider;
import org.hamcrest.Matchers;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookAdvisorApplication.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class PrizeControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken = null;

    @Before
    public void setup() {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");
        adminToken = jwtTokenProvider.createToken("MarioRossi", 1, roles);
    }

    @Test
    public void test1AddPrize_prizeNameNotUnique_bookFound() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer bookId = 1;
        PrizeFormDTO prizeFormDTO = new PrizeFormDTO();
        prizeFormDTO.setYearAwarded(1923);
        prizeFormDTO.setPrizeName("Premio Strega");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(prizeFormDTO);
        mockMvc.perform(post("/api/libri/{id}/prizes", bookId)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.prizeName", hasSize(1)))
                .andExpect(jsonPath("$.prizeName[0]", is("prizeAssigned")));
    }

    @Test
    public void test2AddPrize_bookNotFound() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer bookId = 4;
        PrizeFormDTO prizeFormDTO = new PrizeFormDTO();
        prizeFormDTO.setYearAwarded(1923);
        prizeFormDTO.setPrizeName("Premio strega");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(prizeFormDTO);
        mockMvc.perform(post("/api/libri/{id}/prizes", bookId)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
    }

    @Test
    public void test6AddPrize() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer bookId = 1;
        PrizeFormDTO prizeFormDTO = new PrizeFormDTO();
        prizeFormDTO.setYearAwarded(2002);
        prizeFormDTO.setPrizeName("Premio Bancarella");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(prizeFormDTO);
        mockMvc.perform(post("/api/libri/{id}/prizes", bookId)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.anEmptyMap()));
}

    @Test
    public void test7DeletePrize() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        mockMvc.perform(delete("/api/libri/{id}/prizes/{prizeID}", "1", "1")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie))
                .andExpect(status().isOk());
    }

    @Test
    public void test3GetPrizesOfBook() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(get("/api/libri/{id}/prizes", "1")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].yearAwarded", is(2002)))
                .andExpect(jsonPath("$[0].prizeName", is("Premio Strega")));
    }

    @Test
    public void test4GetPrizeOfBookFromName() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer bookId = 1;
        String prizeName = "Premio Strega";
        mockMvc.perform(get("/api/libri/{id}/prizes/{prizeName}", bookId, prizeName)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.yearAwarded", is(2002)))
                .andExpect(jsonPath("$.prizeName", is(prizeName)));
    }

    @Test
    public void test5IsPrizeAlreadyAssignedToBook() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        String prizeName = "Premio Strega";
        Integer bookId = 1;
        mockMvc.perform(post("/api/libri/{id}/prizes/isPrizeAssigned", bookId)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(prizeName)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));
    }
}
