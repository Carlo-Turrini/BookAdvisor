package com.student.book_advisor.services;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.UsersInfoDTO;
import com.student.book_advisor.data_persistency.model.dto.UtenteCardDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.UtenteFormDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.UtenteUpdateFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Authorities;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.AuthoritiesRepository;
import com.student.book_advisor.data_persistency.repositories.BookRankingRepository;
import com.student.book_advisor.data_persistency.repositories.UsefulReviewRepository;
import com.student.book_advisor.data_persistency.repositories.UsersInfoRepository;
import com.student.book_advisor.services.storage.Constants;
import com.student.book_advisor.services.storage.FileUploadDir;
import com.student.book_advisor.services.storage.StorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
public class UtenteServiceImplUnitTest {
    @TestConfiguration
    static class UtenteServiceImplTestContextConfiguration {
        @Bean
        public UtenteService utenteService() {
            return new UtenteServiceImpl();
        }
    }

    @Autowired
    private UtenteService utenteService;

    @MockBean
    private StorageService storageService;

    @MockBean
    private UsersInfoRepository usersInfoRepository;

    @MockBean
    private AuthoritiesRepository authoritiesRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @MockBean
    private BookRankingRepository bookRankingRepository;

    @MockBean
    private UsefulReviewRepository usefulReviewRepository;

    @Test
    public void testFindAllUsers() {
        List<UtenteCardDTO> utenteCardDTOList = new ArrayList<>();
        UtenteCardDTO utente1 = new UtenteCardDTO(1, "MarioRossi", "Mario", "Rossi");
        UtenteCardDTO utente2 = new UtenteCardDTO(2, "Pablo", "Pablo", "Escobar");
        utenteCardDTOList.add(utente1);
        utenteCardDTOList.add(utente2);
        Mockito.when(usersInfoRepository.findAllUsers()).thenReturn(utenteCardDTOList);
        Mockito.when(usersInfoRepository.getUserProfilePhotoPath(1)).thenReturn(Constants.DEF_PROFILE_PIC);
        Mockito.when(usersInfoRepository.getUserProfilePhotoPath(2)).thenReturn("/notDefault");
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        List<UtenteCardDTO> found = utenteService.findAllUsers();
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(2);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findAllUsers();
        Mockito.verify(usersInfoRepository, Mockito.times(found.size())).getUserProfilePhotoPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testFindById_DefaultPicture() {
        UsersInfoDTO usersInfoDTO = new UsersInfoDTO(1, "Pablo", "Escobar", "Pablo", "p.e@email.com", "Test");
        Mockito.when(usersInfoRepository.findUserById(1)).thenReturn(usersInfoDTO);
        Mockito.when(usersInfoRepository.getUserProfilePhotoPath(Mockito.anyInt())).thenReturn(Constants.DEF_PROFILE_PIC);
        UsersInfoDTO found = utenteService.findById(1);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(usersInfoDTO.getId());
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findUserById(Mockito.anyInt());
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getUserProfilePhotoPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verifyNoInteractions(storageService);
    }

    @Test
    public void testFindById_NotDefaultPicture() {
        UsersInfoDTO usersInfoDTO = new UsersInfoDTO(1, "Pablo", "Escobar", "Pablo", "p.e@email.com", "Test");
        Mockito.when(usersInfoRepository.findUserById(1)).thenReturn(usersInfoDTO);
        Mockito.when(usersInfoRepository.getUserProfilePhotoPath(Mockito.anyInt())).thenReturn("/notDefault");
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        UsersInfoDTO found = utenteService.findById(1);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(usersInfoDTO.getId());
        assertThat(found.getProfilePhoto()).isEqualTo("/test");
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findUserById(Mockito.anyInt());
        Mockito.verify(usersInfoRepository, Mockito.times(1)).getUserProfilePhotoPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testGetUser() {
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setUsername("Prova");
        Mockito.when(usersInfoRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(usersInfo));
        UsersInfo found = utenteService.getUser(1);
        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(usersInfo);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
    }

    @Test
    public void testNewUser() {
        UtenteFormDTO utenteFormDTO = new UtenteFormDTO();
        utenteFormDTO.setNome("Mario");
        utenteFormDTO.setCognome("Rossi");
        utenteFormDTO.setDescrizione("Prova");
        utenteFormDTO.setEmail("m.r@gmail.com");
        utenteFormDTO.setPassword("password");
        utenteFormDTO.setUsername("MarioRossi");
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setUsername("MarioRossi");
        Mockito.when(usersInfoRepository.save(Mockito.any(UsersInfo.class))).thenReturn(usersInfo);
        Mockito.when(authoritiesRepository.save(Mockito.any(Authorities.class))).thenReturn(new Authorities());
        Mockito.when(bCryptPasswordEncoder.encode(Mockito.anyString())).thenReturn("password");
        UsersInfo addedUser = utenteService.newUser(utenteFormDTO);
        assertThat(addedUser).isNotNull();
        assertThat(addedUser).isEqualTo(usersInfo);
        Mockito.verify(bCryptPasswordEncoder, Mockito.times(1)).encode(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(bCryptPasswordEncoder);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).save(Mockito.any(UsersInfo.class));
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(authoritiesRepository, Mockito.times(1)).save(Mockito.any(Authorities.class));
        Mockito.verifyNoMoreInteractions(authoritiesRepository);
    }

    @Test
    public void testUpdateUser_NotNewPassword() {
        UtenteUpdateFormDTO utenteUpdateFormDTO = new UtenteUpdateFormDTO();
        utenteUpdateFormDTO.setCognome("Rossi");
        utenteUpdateFormDTO.setNome("Mario");
        utenteUpdateFormDTO.setDescrizione("Prova");
        utenteUpdateFormDTO.setEmail("m.r@gmail.com");
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setUsername("MarioRossi");
        Mockito.when(usersInfoRepository.save(Mockito.any(UsersInfo.class))).thenReturn(usersInfo);
        UsersInfo updatedUser = utenteService.updateUser(new UsersInfo(), utenteUpdateFormDTO);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(usersInfo);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).save(Mockito.any(UsersInfo.class));
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verifyNoInteractions(bCryptPasswordEncoder);
    }

    @Test
    public void testUpdateUser_NewPassword() {
        UtenteUpdateFormDTO utenteUpdateFormDTO = new UtenteUpdateFormDTO();
        utenteUpdateFormDTO.setCognome("Rossi");
        utenteUpdateFormDTO.setNome("Mario");
        utenteUpdateFormDTO.setDescrizione("Prova");
        utenteUpdateFormDTO.setEmail("m.r@gmail.com");
        utenteUpdateFormDTO.setPassword("newPassword");
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setUsername("MarioRossi");
        Mockito.when(usersInfoRepository.save(Mockito.any(UsersInfo.class))).thenReturn(usersInfo);
        Mockito.when(bCryptPasswordEncoder.encode(Mockito.anyString())).thenReturn("newPassword");
        UsersInfo updatedUser = utenteService.updateUser(new UsersInfo(), utenteUpdateFormDTO);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(usersInfo);
        Mockito.verify(bCryptPasswordEncoder, Mockito.times(1)).encode(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(bCryptPasswordEncoder);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).save(Mockito.any(UsersInfo.class));
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
    }

    @Test
    public void testDeleteUser_userPresent() {
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setProfilePhotoPath("/notDefault");
        Mockito.when(usersInfoRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(usersInfo));
        Mockito.when(bookRankingRepository.findAllByUserID(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(usefulReviewRepository.findAllUsefulReviewsByUserID(Mockito.anyInt())).thenReturn(new ArrayList<>());
        utenteService.deleteUser(1);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).findAllByUserID(Mockito.anyInt());
        Mockito.verify(bookRankingRepository, Mockito.times(1)).deleteInBatch(Mockito.anyIterable());
        Mockito.verifyNoMoreInteractions(bookRankingRepository);
        Mockito.verify(usefulReviewRepository, Mockito.times(1)).findAllUsefulReviewsByUserID(Mockito.anyInt());
        Mockito.verify(usefulReviewRepository, Mockito.times(1)).deleteInBatch(Mockito.anyIterable());
        Mockito.verifyNoMoreInteractions(usefulReviewRepository);
        Mockito.verify(storageService, Mockito.times(1)).delete(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).delete(Mockito.any(UsersInfo.class));
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
    }

    @Test
    public void testDeleteUser_userNotPresent() {
        utenteService.deleteUser(1);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verifyNoInteractions(bookRankingRepository);
        Mockito.verifyNoInteractions(usefulReviewRepository);
        Mockito.verifyNoInteractions(storageService);
    }

    @Test
    public void testIsUsernameUnique_True() {
        Mockito.when(usersInfoRepository.countAllByUsername(Mockito.anyString())).thenReturn(0);
        Boolean unique = utenteService.isUsernameUnique("Test");
        assertThat(unique).isTrue();
        Mockito.verify(usersInfoRepository, Mockito.times(1)).countAllByUsername(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
    }

    @Test
    public void testIsUsernameUnique_False() {
        Mockito.when(usersInfoRepository.countAllByUsername(Mockito.anyString())).thenReturn(1);
        Boolean unique = utenteService.isUsernameUnique("Test");
        assertThat(unique).isFalse();
        Mockito.verify(usersInfoRepository, Mockito.times(1)).countAllByUsername(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
    }

    @Test
    public void testIsEmailUnique_True() {
        Mockito.when(usersInfoRepository.countAllByEmail(Mockito.anyString())).thenReturn(0);
        Boolean unique = utenteService.isEmailUnique("Test");
        assertThat(unique).isTrue();
        Mockito.verify(usersInfoRepository, Mockito.times(1)).countAllByEmail(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
    }

    @Test
    public void testIsEmailUnique_False() {
        Mockito.when(usersInfoRepository.countAllByEmail(Mockito.anyString())).thenReturn(1);
        Boolean unique = utenteService.isEmailUnique("Test");
        assertThat(unique).isFalse();
        Mockito.verify(usersInfoRepository, Mockito.times(1)).countAllByEmail(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
    }

    @Test
    public void testUpdateUsersProfilePhoto() {
        Mockito.when(storageService.serve(Mockito.nullable(String.class), Mockito.any(FileUploadDir.class))).thenReturn("test");
        Mockito.when(storageService.store(Mockito.any(MultipartFile.class), Mockito.any(FileUploadDir.class), Mockito.nullable(String.class))).thenReturn("test");
        MultipartFile mf = new MockMultipartFile("test", "test".getBytes());
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(usersInfo));
        Mockito.when(usersInfoRepository.save(Mockito.any(UsersInfo.class))).thenAnswer(i -> i.getArguments()[0]);
        String src = utenteService.updateUsersProfilePhoto(mf, 1);
        assertThat(src).isNotNull();
        assertThat(src).isEqualTo("{ \"img\":\"test\"}");
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verify(storageService, Mockito.times(1)).store(Mockito.any(MultipartFile.class), Mockito.any(FileUploadDir.class), Mockito.nullable(String.class));
        Mockito.verify(usersInfoRepository, Mockito.times(1)).save(Mockito.any(UsersInfo.class));
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.nullable(String.class), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
    }

    @Test
    public void testUpdateUsersProfilePhoto_userNotFound() {
        Mockito.when(storageService.serve(Mockito.nullable(String.class), Mockito.any(FileUploadDir.class))).thenReturn("test");
        Mockito.when(storageService.store(Mockito.any(MultipartFile.class), Mockito.any(FileUploadDir.class), Mockito.nullable(String.class))).thenReturn("test");
        MultipartFile mf = new MockMultipartFile("test", "test".getBytes());
        UsersInfo usersInfo = new UsersInfo();
        assertThatThrownBy(() -> utenteService.updateUsersProfilePhoto(mf, 1))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("User not found!");
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verifyNoInteractions(storageService);
    }

}
