package com.student.book_advisor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.student.book_advisor.data_persistency.model.dto.UsersInfoDTO;
import com.student.book_advisor.data_persistency.model.dto.UtenteCardDTO;
import com.student.book_advisor.data_persistency.model.dto.auxiliaryDTOs.LoggedUserDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.UtenteFormDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.UtenteUpdateFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Authorities;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.AuthoritiesRepository;
import com.student.book_advisor.data_persistency.repositories.UsersInfoRepository;
import com.student.book_advisor.security.JwtTokenProvider;
import com.student.book_advisor.security.SecurityConfiguration;
import com.student.book_advisor.security.UserDetailsServiceImpl;
import com.student.book_advisor.security.redis.RedisUtil;
import com.student.book_advisor.services.UtenteService;
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
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UtenteController.class)
@ContextConfiguration(classes = {SecurityConfiguration.class, JwtTokenProvider.class, RedisUtil.class, UserDetailsServiceImpl.class, UtenteController.class})
public class UtenteControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UtenteService utenteService;

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
    public void testGetLoggedUserInfo() throws Exception {
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
    public void testGetLoggedUserInfo_unauthorized() throws Exception {
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
    public void testVerifyUsernameUniqueness() throws Exception {
        String usernmaeUnique = "MarioRossi";
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Mockito.when(utenteService.isUsernameUnique(usernmaeUnique)).thenReturn(true);
        mockMvc.perform(post("/utenti/isUsernameUnique")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(usernmaeUnique)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
        Mockito.verify(utenteService, Mockito.times(1)).isUsernameUnique(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testVerifyEmailUniqueness() throws Exception {
        String emailUnique = "m.r@gmail.com";
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Mockito.when(utenteService.isEmailUnique(emailUnique)).thenReturn(true);
        mockMvc.perform(post("/utenti/isEmailUnique")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(emailUnique)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
        Mockito.verify(utenteService, Mockito.times(1)).isEmailUnique(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testGetAllUsers() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        List<UtenteCardDTO> utenteCardDTOList = new ArrayList<>();
        UtenteCardDTO utenteCardDTO = new UtenteCardDTO(1, "MarioRossi", "Mario", "Rossi");
        utenteCardDTOList.add(utenteCardDTO);
        Mockito.when(utenteService.findAllUsers()).thenReturn(utenteCardDTOList);
        mockMvc.perform(get("/utenti")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(utenteCardDTO.getId())))
                .andExpect(jsonPath("$[0].username", is(utenteCardDTO.getUsername())))
                .andExpect(jsonPath("$[0].nome", is(utenteCardDTO.getNome())))
                .andExpect(jsonPath("$[0].cognome", is(utenteCardDTO.getCognome())));
        Mockito.verify(utenteService, Mockito.times(1)).findAllUsers();
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testGetAllUsers_unauthorized() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        mockMvc.perform(get("/utenti")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        Mockito.verifyNoInteractions(utenteService);
    }

    @Test
    public void testGetUser() throws Exception {
        Integer userID = 1;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        UsersInfoDTO usersInfoDTO = new UsersInfoDTO(userID, "Mario", "Rossi", "MarioRossi", "m.r@gmail.com", "prova");
        Mockito.when(utenteService.findById(userID)).thenReturn(usersInfoDTO);
        mockMvc.perform(get("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(usersInfoDTO.getId())))
                .andExpect(jsonPath("$.username", is(usersInfoDTO.getUsername())))
                .andExpect(jsonPath("$.name", is(usersInfoDTO.getName())))
                .andExpect(jsonPath("$.surname", is(usersInfoDTO.getSurname())))
                .andExpect(jsonPath("$.email", is(usersInfoDTO.getEmail())))
                .andExpect(jsonPath("$.description", is(usersInfoDTO.getDescription())));
        Mockito.verify(utenteService, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testNewUser_emailNotUnique_usernameNotUnique() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        UtenteFormDTO utenteFormDTO = new UtenteFormDTO();
        utenteFormDTO.setUsername("MarioRossi");
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setEmail("m.r@gmail.com");
        utenteFormDTO.setPassword("password");
        utenteFormDTO.setDescrizione("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(utenteFormDTO);
        Mockito.when(utenteService.isEmailUnique(utenteFormDTO.getEmail())).thenReturn(false);
        Mockito.when(utenteService.isUsernameUnique(utenteFormDTO.getUsername())).thenReturn(false);
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
        Mockito.verify(utenteService, Mockito.times(1)).isUsernameUnique(Mockito.anyString());
        Mockito.verify(utenteService, Mockito.times(1)).isEmailUnique(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testNewUser() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        UtenteFormDTO utenteFormDTO = new UtenteFormDTO();
        utenteFormDTO.setUsername("MarioRossi");
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setEmail("m.r@gmail.com");
        utenteFormDTO.setPassword("password");
        utenteFormDTO.setDescrizione("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(utenteFormDTO);
        Mockito.when(utenteService.isEmailUnique(utenteFormDTO.getEmail())).thenReturn(true);
        Mockito.when(utenteService.isUsernameUnique(utenteFormDTO.getUsername())).thenReturn(true);
        mockMvc.perform(post("/utenti")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", anEmptyMap()));
        Mockito.verify(utenteService, Mockito.times(1)).isUsernameUnique(Mockito.anyString());
        Mockito.verify(utenteService, Mockito.times(1)).isEmailUnique(Mockito.anyString());
        Mockito.verify(utenteService, Mockito.times(1)).newUser(Mockito.any(UtenteFormDTO.class));
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testUpdateUsersProfilePhoto_userAuth() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        MockMultipartFile mf = new MockMultipartFile("fotoProfilo", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        Integer userID = 2;
        Mockito.when(utenteService.updateUsersProfilePhoto(mf, userID)).thenReturn("prova");
        mockMvc.perform(multipart("/utenti/{id}/foto_profilo", userID)
                .file(mf)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
                .andExpect(jsonPath("$", is("prova")));
        Mockito.verify(utenteService, Mockito.times(1)).updateUsersProfilePhoto(Mockito.any(MockMultipartFile.class), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testUpdateUsersProfilePhoto_adminAuth() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        MockMultipartFile mf = new MockMultipartFile("fotoProfilo", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        Integer userID = 2;
        Mockito.when(utenteService.updateUsersProfilePhoto(mf, userID)).thenReturn("prova");
        mockMvc.perform(multipart("/utenti/{id}/foto_profilo", userID)
                .file(mf)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=UTF-8"))
                .andExpect(jsonPath("$", is("prova")));
        Mockito.verify(utenteService, Mockito.times(1)).updateUsersProfilePhoto(Mockito.any(MockMultipartFile.class), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testUpdateUsersProfilePhoto_unauthorized() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        MockMultipartFile mf = new MockMultipartFile("fotoProfilo", "test.png", MediaType.IMAGE_PNG_VALUE, "test".getBytes());
        Integer userID = 2;
        Mockito.when(utenteService.updateUsersProfilePhoto(mf, userID)).thenReturn("prova");
        mockMvc.perform(multipart("/utenti/{id}/foto_profilo", userID)
                .file(mf)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie))
                .andExpect(status().isUnauthorized());
        Mockito.verifyNoInteractions(utenteService);
    }

    @Test
    public void testUpdateUser_unauthorized() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        UtenteUpdateFormDTO utenteFormDTO = new UtenteUpdateFormDTO();
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setEmail("m.r@gmail.com");
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
        Mockito.verifyNoInteractions(utenteService);
    }

    @Test
    public void testUpdateUser_userNotFound() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        UtenteUpdateFormDTO utenteFormDTO = new UtenteUpdateFormDTO();
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setEmail("m.r@gmail.com");
        utenteFormDTO.setPassword(null);
        utenteFormDTO.setDescrizione("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(utenteFormDTO);
        Mockito.when(utenteService.getUser(userID)).thenReturn(null);
        mockMvc.perform(put("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
        Mockito.verify(utenteService, Mockito.times(1)).getUser(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testUpdateUser_userAuth_emailChangedTaken() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        UtenteUpdateFormDTO utenteFormDTO = new UtenteUpdateFormDTO();
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setEmail("m.r@gmail.com");
        utenteFormDTO.setPassword(null);
        utenteFormDTO.setDescrizione("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(utenteFormDTO);
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setEmail("mario.rossi@gmail.com");
        Mockito.when(utenteService.getUser(userID)).thenReturn(usersInfo);
        Mockito.when(utenteService.isEmailUnique(utenteFormDTO.getEmail())).thenReturn(false);
        mockMvc.perform(put("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", hasSize(1)))
                .andExpect(jsonPath("$.email[0]", is("emailTaken")));
        Mockito.verify(utenteService, Mockito.times(1)).getUser(Mockito.anyInt());
        Mockito.verify(utenteService, Mockito.times(1)).isEmailUnique(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testUpdateUser_userAuth_passwordChangedMinLength() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", userToken);
        UtenteUpdateFormDTO utenteFormDTO = new UtenteUpdateFormDTO();
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setEmail("m.r@gmail.com");
        utenteFormDTO.setPassword("pass");
        utenteFormDTO.setDescrizione("prova");
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(utenteFormDTO);
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setEmail(utenteFormDTO.getEmail());
        Mockito.when(utenteService.getUser(userID)).thenReturn(usersInfo);
        mockMvc.perform(put("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password", hasSize(1)))
                .andExpect(jsonPath("$.password[0]", is("minlength")));
        Mockito.verify(utenteService, Mockito.times(1)).getUser(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testUpdateUser_userAuth() throws Exception {
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
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setEmail(utenteFormDTO.getEmail());
        Mockito.when(utenteService.getUser(userID)).thenReturn(usersInfo);
        mockMvc.perform(put("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
        Mockito.verify(utenteService, Mockito.times(1)).getUser(Mockito.anyInt());
        Mockito.verify(utenteService, Mockito.times(1)).updateUser(Mockito.any(UsersInfo.class), Mockito.any(UtenteUpdateFormDTO.class));
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testUpdateUser_adminAuth() throws Exception {
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
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setEmail(utenteFormDTO.getEmail());
        Mockito.when(utenteService.getUser(userID)).thenReturn(usersInfo);
        mockMvc.perform(put("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anEmptyMap()));
        Mockito.verify(utenteService, Mockito.times(1)).getUser(Mockito.anyInt());
        Mockito.verify(utenteService, Mockito.times(1)).updateUser(Mockito.any(UsersInfo.class), Mockito.any(UtenteUpdateFormDTO.class));
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testDeleteUser_userAuth() throws Exception {
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
        Mockito.verify(utenteService, Mockito.times(1)).deleteUser(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testDeleteUser_adminAuth() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        mockMvc.perform(delete("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(utenteService, Mockito.times(1)).deleteUser(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(utenteService);
    }

    @Test
    public void testDeleteUser_unauthorized() throws Exception {
        Integer userID = 2;
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        mockMvc.perform(delete("/utenti/{id}", userID)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        Mockito.verifyNoInteractions(utenteService);
    }
}
