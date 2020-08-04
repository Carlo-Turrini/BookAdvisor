package com.student.book_advisor.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.student.book_advisor.data_persistency.model.dto.PrizeDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.PrizeFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Authorities;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.AuthoritiesRepository;
import com.student.book_advisor.data_persistency.repositories.UsersInfoRepository;
import com.student.book_advisor.security.JwtTokenProvider;
import com.student.book_advisor.security.SecurityConfiguration;
import com.student.book_advisor.security.UserDetailsServiceImpl;
import com.student.book_advisor.security.redis.RedisUtil;
import com.student.book_advisor.services.LibroService;
import com.student.book_advisor.services.PrizeService;
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
@WebMvcTest(controllers = PrizeController.class)
@ContextConfiguration(classes = {SecurityConfiguration.class, JwtTokenProvider.class, RedisUtil.class, UserDetailsServiceImpl.class, PrizeController.class})
public class PrizeControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private PrizeService prizeService;

    @MockBean
    private LibroService libroService;

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
    public void testAddPrize_prizeNameNotUnique_bookFound() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer bookId = 1;
        PrizeFormDTO prizeFormDTO = new PrizeFormDTO();
        prizeFormDTO.setYearAwarded(1923);
        prizeFormDTO.setPrizeName("Premio strega");
        Mockito.when(prizeService.isPrizeAlreadyAssignedToBook(prizeFormDTO.getPrizeName(), bookId)).thenReturn(true);
        Mockito.when(libroService.findBookById(bookId)).thenReturn(new Libro());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(prizeFormDTO);
        mockMvc.perform(post("/libri/{id}/prizes", bookId)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.prizeName", hasSize(1)))
                .andExpect(jsonPath("$.prizeName[0]", is("prizeAssigned")));
        Mockito.verify(libroService, Mockito.times(1)).findBookById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroService);
        Mockito.verify(prizeService, Mockito.times(1)).isPrizeAlreadyAssignedToBook(Mockito.anyString(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(prizeService);
    }

    @Test
    public void testAddPrize_bookNotFound() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer bookId = 1;
        PrizeFormDTO prizeFormDTO = new PrizeFormDTO();
        prizeFormDTO.setYearAwarded(1923);
        prizeFormDTO.setPrizeName("Premio strega");
        Mockito.when(libroService.findBookById(bookId)).thenReturn(null);
        Mockito.when(prizeService.isPrizeAlreadyAssignedToBook(prizeFormDTO.getPrizeName(), bookId)).thenReturn(false);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(prizeFormDTO);
        mockMvc.perform(post("/libri/{id}/prizes", bookId)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException() instanceof ResponseStatusException).isTrue());
        Mockito.verify(libroService, Mockito.times(1)).findBookById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroService);
        Mockito.verifyNoInteractions(prizeService);
    }

    @Test
    public void testAddPrize() throws Exception{
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        Integer bookId = 1;
        PrizeFormDTO prizeFormDTO = new PrizeFormDTO();
        prizeFormDTO.setYearAwarded(1923);
        prizeFormDTO.setPrizeName("Premio strega");
        Mockito.when(libroService.findBookById(bookId)).thenReturn(new Libro());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
        String requestJson = objectWriter.writeValueAsString(prizeFormDTO);
        mockMvc.perform(post("/libri/{id}/prizes", bookId)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(requestJson)
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.anEmptyMap()));
        Mockito.verify(libroService, Mockito.times(1)).findBookById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroService);
        Mockito.verify(prizeService, Mockito.times(1)).isPrizeAlreadyAssignedToBook(Mockito.anyString(), Mockito.anyInt());
        Mockito.verify(prizeService, Mockito.times(1)).addPrize(Mockito.any(PrizeFormDTO.class), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(prizeService);    }

    @Test
    public void testDeletePrize() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        mockMvc.perform(delete("/libri/{id}/prizes/{prizeID}", "1", "2")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie))
                .andExpect(status().isOk());
        Mockito.verify(prizeService, Mockito.times(1)).deletePrize(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(prizeService);
    }

    @Test
    public void testGetPrizesOfBook() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        List<PrizeDTO> prizeDTOList = new ArrayList<>();
        PrizeDTO prizeDTO = new PrizeDTO(1, 1923, "Premio strega");
        prizeDTOList.add(prizeDTO);
        Mockito.when(prizeService.getAllPrizesOfBook(Mockito.anyInt())).thenReturn(prizeDTOList);
        mockMvc.perform(get("/libri/{id}/prizes", "1")
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].yearAwarded", is(1923)))
                .andExpect(jsonPath("$[0].prizeName", is("Premio strega")));
        Mockito.verify(prizeService, Mockito.times(1)).getAllPrizesOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(prizeService);
    }

    @Test
    public void testGetPrizeOfBookFromName() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        PrizeDTO prizeDTO = new PrizeDTO(1, 1923, "Premio strega");
        Integer bookId = 1;
        Mockito.when(prizeService.getPrizeOfBookFromName(bookId, prizeDTO.getPrizeName())).thenReturn(prizeDTO);
        mockMvc.perform(get("/libri/{id}/prizes/{prizeName}", bookId, prizeDTO.getPrizeName())
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(prizeDTO.getId())))
                .andExpect(jsonPath("$.yearAwarded", is(prizeDTO.getYearAwarded())))
                .andExpect(jsonPath("$.prizeName", is(prizeDTO.getPrizeName())));
        Mockito.verify(prizeService, Mockito.times(1)).getPrizeOfBookFromName(Mockito.anyInt(), Mockito.anyString());
        Mockito.verifyNoMoreInteractions(prizeService);
    }

    @Test
    public void testIsPrizeAlreadyAssignedToBook() throws Exception {
        UUID csrf = UUID.randomUUID();
        Cookie csrfCookie = new Cookie("XSRF-TOKEN", csrf.toString());
        Cookie authCookie = new Cookie("access_token", adminToken);
        String prizeName = "Premio strega";
        Integer bookId = 1;
        Mockito.when(prizeService.isPrizeAlreadyAssignedToBook(prizeName, bookId)).thenReturn(true);
        mockMvc.perform(post("/libri/{id}/prizes/isPrizeAssigned", bookId)
                .header("X-XSRF-TOKEN", csrf.toString())
                .cookie(csrfCookie, authCookie)
                .content(prizeName)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(true)));
        Mockito.verify(prizeService, Mockito.times(1)).isPrizeAlreadyAssignedToBook(Mockito.anyString(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(prizeService);
    }
}
