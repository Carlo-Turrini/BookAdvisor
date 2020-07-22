package com.student.book_advisor.integrationTesting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.student.book_advisor.BookAdvisorApplication;
import com.student.book_advisor.data_persistency.model.dto.UsersInfoDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.LoggedUserDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.UtenteFormDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.UtenteUpdateFormDTO;
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
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookAdvisorApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:integrationtest.properties")
@Transactional
@Rollback
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UtenteControllerIntegrationTest {
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
    public void testAGetLoggedUserInfo() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        LoggedUserDTO loggedUserDTO = new LoggedUserDTO(2, false);
        mockMvc.perform(get("/utenti/loggedUserInfo")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(loggedUserDTO.getId())))
                .andExpect(jsonPath("$.admin", is(loggedUserDTO.getAdmin())));
    }

    @Test
    public void testBGetLoggedUserInfo_unauthorized() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(get("/utenti/loggedUserInfo")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCVerifyUsernameUniqueness() throws Exception {
        String usernameUnique = "MarioRossi";
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(post("/utenti/isUsernameUnique")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(usernameUnique)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));
    }

    @Test
    public void testDVerifyEmailUniqueness() throws Exception {
        String emailUnique = "m.r@gmail.com";
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(post("/utenti/isEmailUnique")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(emailUnique)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
    }

    @Test
    public void testEGetAllUsers() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        mockMvc.perform(get("/utenti")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("MarioRossi")))
                .andExpect(jsonPath("$[0].nome", is("Mario")))
                .andExpect(jsonPath("$[0].cognome", is("Rossi")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].username", is("Tarlo")))
                .andExpect(jsonPath("$[1].nome", is("Carlo")))
                .andExpect(jsonPath("$[1].cognome", is("Turrini")));
    }

    @Test
    public void testFGetAllUsers_unauthorized() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        mockMvc.perform(get("/utenti")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGGetUser() throws Exception {
        Integer userID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        UsersInfoDTO usersInfoDTO = new UsersInfoDTO(userID, "Mario", "Rossi", "MarioRossi", "mario.rossi@gmail.com", "prova");
        mockMvc.perform(get("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is(usersInfoDTO.getUsername())))
                .andExpect(jsonPath("$.name", is(usersInfoDTO.getName())))
                .andExpect(jsonPath("$.surname", is(usersInfoDTO.getSurname())))
                .andExpect(jsonPath("$.email", is(usersInfoDTO.getEmail())))
                .andExpect(jsonPath("$.description", is(usersInfoDTO.getDescription())));
    }

    @Test
    public void testHNewUser_emailNotUnique_usernameNotUnique() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        UtenteFormDTO utenteFormDTO = new UtenteFormDTO();
        utenteFormDTO.setUsername("MarioRossi");
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setEmail("mario.rossi@gmail.com");
        utenteFormDTO.setPassword("password");
        utenteFormDTO.setDescrizione("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(utenteFormDTO);
        mockMvc.perform(post("/utenti")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", hasSize(1)))
                .andExpect(jsonPath("$.email[0]", is("emailTaken")))
                .andExpect(jsonPath("$.username", hasSize(1)))
                .andExpect(jsonPath("$.username[0]", is("usernameTaken")));
    }

    @Test
    public void testINewUser() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        UtenteFormDTO utenteFormDTO = new UtenteFormDTO();
        utenteFormDTO.setUsername("GustavoBianchi");
        utenteFormDTO.setNome("Gustavo");
        utenteFormDTO.setCognome("Bianchi");
        utenteFormDTO.setEmail("g.b@gmail.com");
        utenteFormDTO.setPassword("password");
        utenteFormDTO.setDescrizione("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(utenteFormDTO);
        mockMvc.perform(post("/utenti")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", anEmptyMap()));
    }

    @Test
    public void testJUpdateUsersProfilePhoto_userAuth() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        MockMultipartFile mf = new MockMultipartFile("fotoProfilo", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        Integer userID = 2;
        mockMvc.perform(multipart("/utenti/{id}/foto_profilo", userID)
                .file(mf)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(jsonPath("$.img", notNullValue()));
    }

    @Test
    public void testKUpdateUsersProfilePhoto_adminAuth() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        MockMultipartFile mf = new MockMultipartFile("fotoProfilo", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        Integer userID = 2;
        mockMvc.perform(multipart("/utenti/{id}/foto_profilo", userID)
                .file(mf)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(jsonPath("$.img", notNullValue()));
    }

    @Test
    public void testLUpdateUsersProfilePhoto_unauthorized() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        MockMultipartFile mf = new MockMultipartFile("fotoProfilo", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        Integer userID = 2;
        mockMvc.perform(multipart("/utenti/{id}/foto_profilo", userID)
                .file(mf)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testMUpdateUser_unauthorized() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        UtenteUpdateFormDTO utenteFormDTO = new UtenteUpdateFormDTO();
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setEmail("carlo.turrini@gmail.com");
        utenteFormDTO.setPassword(null);
        utenteFormDTO.setDescrizione("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(utenteFormDTO);
        mockMvc.perform(put("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testNUpdateUser_userNotFound() throws Exception {
        Integer userID = 4;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        UtenteUpdateFormDTO utenteFormDTO = new UtenteUpdateFormDTO();
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setEmail("carlo.turrini@gmail.com");
        utenteFormDTO.setPassword(null);
        utenteFormDTO.setDescrizione("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(utenteFormDTO);
        assertThatThrownBy(() -> mockMvc.perform(put("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()))
                .isInstanceOf(NestedServletException.class);
    }

    @Test
    public void testOUpdateUser_userAuth_emailChangedTaken() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        UtenteUpdateFormDTO utenteFormDTO = new UtenteUpdateFormDTO();
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setEmail("mario.rossi@gmail.com");
        utenteFormDTO.setPassword(null);
        utenteFormDTO.setDescrizione("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(utenteFormDTO);
        mockMvc.perform(put("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", hasSize(1)))
                .andExpect(jsonPath("$.email[0]", is("emailTaken")));
    }

    @Test
    public void testPUpdateUser_userAuth_passwordChangedMinLength() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        UtenteUpdateFormDTO utenteFormDTO = new UtenteUpdateFormDTO();
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setEmail("carlo.turrini@gmail.com");
        utenteFormDTO.setPassword("pass");
        utenteFormDTO.setDescrizione("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(utenteFormDTO);
        mockMvc.perform(put("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password", hasSize(1)))
                .andExpect(jsonPath("$.password[0]", is("minlength")));
    }

    @Test
    public void testQUpdateUser_userAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        UtenteUpdateFormDTO utenteFormDTO = new UtenteUpdateFormDTO();
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setEmail("m.r@gmail.com");
        utenteFormDTO.setPassword("password");
        utenteFormDTO.setDescrizione("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(utenteFormDTO);
        mockMvc.perform(put("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
    }

    @Test
    public void testRUpdateUser_adminAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        UtenteUpdateFormDTO utenteFormDTO = new UtenteUpdateFormDTO();
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setEmail("m.r@gmail.com");
        utenteFormDTO.setPassword("password");
        utenteFormDTO.setDescrizione("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(utenteFormDTO);
        mockMvc.perform(put("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
    }

    @Test
    public void testSDeleteUser_userAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        mockMvc.perform(delete("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist("access_token"));
    }

    @Test
    public void testTDeleteUser_adminAuth() throws Exception {
        Integer userID = 3;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        mockMvc.perform(delete("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUDeleteUser_unauthorized() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(delete("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
