package com.student.book_advisor.services;


import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.RecensioneDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.RecensioneFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.Recensione;
import com.student.book_advisor.data_persistency.model.entities.UsefulReview;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.*;
import com.student.book_advisor.security.AuthUserPrincipal;
import com.student.book_advisor.services.storage.Constants;
import com.student.book_advisor.services.storage.FileUploadDir;
import com.student.book_advisor.services.storage.StorageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
public class RecensioneServiceImplUnitTest {
    @TestConfiguration
    static class RecensioneServiceImplTestContextConfiguration {
        @Bean
        public RecensioneService recensioneService() {
            return new RecensioneServiceImpl();
        }
    }

    @Autowired
    private RecensioneService recensioneService;

    @MockBean
    private RecensioneRepository recensioneRepository;

    @MockBean
    private StorageService storageService;

    @MockBean
    private UsersInfoRepository usersInfoRepository;

    @MockBean
    private LibroRepository libroRepository;

    @MockBean
    private UsefulReviewRepository usefulReviewRepository;

    @MockBean
    private AuthorRepository authorRepository;

    private SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    private Authentication authentication = Mockito.mock(Authentication.class);


    private AuthUserPrincipal authUserPrincipal = null;

    @Before
    public void setup() {
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setUsername("MarioRossi");
        usersInfo.setId(1);
        authUserPrincipal = new AuthUserPrincipal(usersInfo, null);
        //Set securityContext
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(authUserPrincipal);
    }

    @Test
    public void testGetAllReviewsByBook_defaultCover() {
        RecensioneDTO recensioneDTO1 = new RecensioneDTO(1, "Prova1", 4, Calendar.getInstance().getTime(), 1, "MarioRossi", 1, "Titolo1", 3, 4, 4, false, 12L);
        RecensioneDTO recensioneDTO2 = new RecensioneDTO(2, "Prova2", 4, Calendar.getInstance().getTime(), 2, "MarioRossi", 1, "Titolo2", 3, 4, 4, false, 12L);
        List<RecensioneDTO> recensioneDTOList = new ArrayList<>();
        recensioneDTOList.add(recensioneDTO1);
        recensioneDTOList.add(recensioneDTO2);
        Mockito.when(recensioneRepository.findAllByBook(1)).thenReturn(recensioneDTOList);
        Mockito.when(libroRepository.findBookCoverPath(Mockito.anyInt())).thenReturn(Constants.DEF_BOOK_COVER);
        Mockito.when(usersInfoRepository.getUserProfilePhotoPath(1)).thenReturn(Constants.DEF_PROFILE_PIC);
        Mockito.when(usersInfoRepository.getUserProfilePhotoPath(2)).thenReturn("/notDefaultImage");
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        List<RecensioneDTO> found = recensioneService.getAllReviewsByBook(1);
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(recensioneDTOList.size());
        Mockito.verify(recensioneRepository, Mockito.times(1)).findAllByBook(Mockito.anyInt());
        Mockito.verify(libroRepository, Mockito.times(found.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(usersInfoRepository, Mockito.times(found.size())).getUserProfilePhotoPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(storageService, Mockito.times(1)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
        Mockito.verify(authorRepository, Mockito.times(found.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(usefulReviewRepository, Mockito.times(found.size())).findByUserIDAndReviewID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usefulReviewRepository);
    }

    @Test
    public void testGetAllReviewsByBook_notDefaultCover() {
        RecensioneDTO recensioneDTO1 = new RecensioneDTO(1, "Prova1", 4, Calendar.getInstance().getTime(), 1, "MarioRossi", 1, "Titolo1", 3, 4, 4, false, 12L);
        RecensioneDTO recensioneDTO2 = new RecensioneDTO(2, "Prova2", 4, Calendar.getInstance().getTime(), 2, "MarioRossi", 1, "Titolo2", 3, 4, 4, false, 12L);
        List<RecensioneDTO> recensioneDTOList = new ArrayList<>();
        recensioneDTOList.add(recensioneDTO1);
        recensioneDTOList.add(recensioneDTO2);
        Mockito.when(recensioneRepository.findAllByBook(1)).thenReturn(recensioneDTOList);
        Mockito.when(libroRepository.findBookCoverPath(Mockito.anyInt())).thenReturn("/notDefaultImage");
        Mockito.when(usersInfoRepository.getUserProfilePhotoPath(1)).thenReturn(Constants.DEF_PROFILE_PIC);
        Mockito.when(usersInfoRepository.getUserProfilePhotoPath(2)).thenReturn("/notDefaultImage");
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        List<RecensioneDTO> found = recensioneService.getAllReviewsByBook(1);
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(recensioneDTOList.size());
        Mockito.verify(recensioneRepository, Mockito.times(1)).findAllByBook(Mockito.anyInt());
        Mockito.verify(libroRepository, Mockito.times(found.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(usersInfoRepository, Mockito.times(found.size())).getUserProfilePhotoPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(storageService, Mockito.times(3)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
        Mockito.verify(authorRepository, Mockito.times(found.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(usefulReviewRepository, Mockito.times(found.size())).findByUserIDAndReviewID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usefulReviewRepository);
    }

    @Test
    public void testGetAllReviewsByUser_defaultProfilePic() {
        RecensioneDTO recensioneDTO1 = new RecensioneDTO(1, "Prova1", 4, Calendar.getInstance().getTime(), 1, "MarioRossi", 1, "Titolo1", 3, 4, 4, false, 12L);
        RecensioneDTO recensioneDTO2 = new RecensioneDTO(2, "Prova2", 4, Calendar.getInstance().getTime(), 1, "MarioRossi", 2, "Titolo2", 3, 4, 4, false, 12L);
        List<RecensioneDTO> recensioneDTOList = new ArrayList<>();
        recensioneDTOList.add(recensioneDTO1);
        recensioneDTOList.add(recensioneDTO2);
        Mockito.when(recensioneRepository.findAllByUser(1)).thenReturn(recensioneDTOList);
        Mockito.when(libroRepository.findBookCoverPath(Mockito.anyInt())).thenReturn("/notDefaultImage");
        Mockito.when(usersInfoRepository.getUserProfilePhotoPath(1)).thenReturn(Constants.DEF_PROFILE_PIC);
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        List<RecensioneDTO> found = recensioneService.getAllReveiewsByUser(1);
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(recensioneDTOList.size());
        Mockito.verify(recensioneRepository, Mockito.times(1)).findAllByUser(Mockito.anyInt());
        Mockito.verify(libroRepository, Mockito.times(found.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(usersInfoRepository, Mockito.times(found.size())).getUserProfilePhotoPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(storageService, Mockito.times(2)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
        Mockito.verify(authorRepository, Mockito.times(found.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(usefulReviewRepository, Mockito.times(found.size())).findByUserIDAndReviewID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usefulReviewRepository);
    }

    @Test
    public void testGetAllReviewsByUser_notDefaultProfilePic() {
        RecensioneDTO recensioneDTO1 = new RecensioneDTO(1, "Prova1", 4, Calendar.getInstance().getTime(), 1, "MarioRossi", 1, "Titolo1", 3, 4, 4, false, 12L);
        RecensioneDTO recensioneDTO2 = new RecensioneDTO(2, "Prova2", 4, Calendar.getInstance().getTime(), 1, "MarioRossi", 2, "Titolo2", 3, 4, 4, false, 12L);
        List<RecensioneDTO> recensioneDTOList = new ArrayList<>();
        recensioneDTOList.add(recensioneDTO1);
        recensioneDTOList.add(recensioneDTO2);
        Mockito.when(recensioneRepository.findAllByUser(1)).thenReturn(recensioneDTOList);
        Mockito.when(libroRepository.findBookCoverPath(Mockito.anyInt())).thenReturn("/notDefaultImage");
        Mockito.when(usersInfoRepository.getUserProfilePhotoPath(1)).thenReturn("/notDefaultImage");
        Mockito.when(storageService.serve(Mockito.anyString(), Mockito.any(FileUploadDir.class))).thenReturn("/test");
        Mockito.when(authorRepository.findAuthorsOfBook(Mockito.anyInt())).thenReturn(new ArrayList<>());
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        List<RecensioneDTO> found = recensioneService.getAllReveiewsByUser(1);
        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(recensioneDTOList.size());
        Mockito.verify(recensioneRepository, Mockito.times(1)).findAllByUser(Mockito.anyInt());
        Mockito.verify(libroRepository, Mockito.times(found.size())).findBookCoverPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(usersInfoRepository, Mockito.times(found.size())).getUserProfilePhotoPath(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(storageService, Mockito.times(4)).serve(Mockito.anyString(), Mockito.any(FileUploadDir.class));
        Mockito.verifyNoMoreInteractions(storageService);
        Mockito.verify(authorRepository, Mockito.times(found.size())).findAuthorsOfBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authorRepository);
        Mockito.verify(usefulReviewRepository, Mockito.times(found.size())).findByUserIDAndReviewID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usefulReviewRepository);
    }


    @Test
    public void testAddNewReview_bookPresent() {
        RecensioneFormDTO recensioneFormDTO = new RecensioneFormDTO();
        Recensione recensione = new Recensione();
        Libro book = new Libro();
        Mockito.when(libroRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(book));
        Mockito.when(recensioneRepository.save(Mockito.any(Recensione.class))).thenReturn(recensione);
        Recensione addedReview = recensioneService.addNewReview(recensioneFormDTO, 1);
        assertThat(addedReview).isNotNull();
        assertThat(addedReview).isEqualTo(recensione);
        Mockito.verify(libroRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(securityContext, Mockito.times(2)).getAuthentication();
        Mockito.verifyNoMoreInteractions(securityContext);
        Mockito.verify(authentication, Mockito.times(1)).getPrincipal();
        Mockito.verifyNoMoreInteractions(authentication);
        Mockito.verify(recensioneRepository, Mockito.times(1)).save(Mockito.any(Recensione.class));
        Mockito.verifyNoMoreInteractions(recensioneRepository);
    }

    @Test
    public void testAddNewReview_bookNotPresent() {
        RecensioneFormDTO recensioneFormDTO = new RecensioneFormDTO();
        assertThatThrownBy(() -> recensioneService.addNewReview(recensioneFormDTO, 1))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("Libro inesistente!");
        Mockito.verify(libroRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verifyNoInteractions(recensioneRepository);
        Mockito.verifyNoInteractions(authentication);
    }

    @Test
    public void testDeleteReview() {
        Recensione recensione = new Recensione();
        Mockito.when(recensioneRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(recensione));
        recensioneService.deleteReview(1);
        Mockito.verify(recensioneRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verify(recensioneRepository, Mockito.times(1)).delete(Mockito.any(Recensione.class));
        Mockito.verifyNoMoreInteractions(recensioneRepository);
    }

    @Test
    public void testGetReview() {
        Recensione recensione = new Recensione();
        Mockito.when(recensioneRepository.getOne(Mockito.anyInt())).thenReturn(recensione);
        Recensione found = recensioneService.getReview(1);
        assertThat(found).isEqualTo(recensione);
        Mockito.verify(recensioneRepository, Mockito.times(1)).getOne(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneRepository);
    }

    @Test
    public void testAddUsefulReview_userNull() {
        assertThatThrownBy(() -> recensioneService.addUsefulReview(1, 2))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("User non esistente");
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verifyNoInteractions(recensioneRepository);
        Mockito.verifyNoInteractions(usefulReviewRepository);
    }

    @Test
    public void testAddUsefulReview_reviewNull() {
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(usersInfo));
        assertThatThrownBy(() -> recensioneService.addUsefulReview(1, 2))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("Recensione non esistente");
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(recensioneRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneRepository);
        Mockito.verifyNoInteractions(usefulReviewRepository);
    }

    @Test
    public void testAddUsefulReview_usefulReviewNotNull() {
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(usersInfo));
        Recensione recensione = new Recensione();
        Mockito.when(recensioneRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(recensione));
        UsefulReview ur = new UsefulReview();
        Mockito.when(usefulReviewRepository.findByUserIDAndReviewID(Mockito.anyInt(), Mockito.anyInt())).thenReturn(ur);
        assertThatThrownBy(() -> recensioneService.addUsefulReview(1, 2))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("Mi piace già messo");
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(recensioneRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneRepository);
        Mockito.verify(usefulReviewRepository, Mockito.times(1)).findByUserIDAndReviewID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usefulReviewRepository);
    }

    @Test
    public void testAddUsefulReview() {
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(usersInfo));
        Recensione recensione = new Recensione();
        Mockito.when(recensioneRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(recensione));
        Mockito.when(usefulReviewRepository.findByUserIDAndReviewID(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);
        recensioneService.addUsefulReview(1, 2);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(recensioneRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneRepository);
        Mockito.verify(usefulReviewRepository, Mockito.times(1)).findByUserIDAndReviewID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(usefulReviewRepository, Mockito.times(1)).save(Mockito.any(UsefulReview.class));
        Mockito.verifyNoMoreInteractions(usefulReviewRepository);
    }

    @Test
    public void testRemoveUsefulReview_userNull() {
        recensioneService.removeUsefulReview(1, 2);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verifyNoInteractions(recensioneRepository);
        Mockito.verifyNoInteractions(usefulReviewRepository);
    }

    @Test
    public void testRemoveUsefulReview_ReviewNull() {
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(usersInfo));
        recensioneService.removeUsefulReview(1, 2);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(recensioneRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneRepository);
        Mockito.verifyNoInteractions(usefulReviewRepository);
    }

    @Test
    public void testRemoveUsefulReview_UsefulReviewNull() {
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(usersInfo));
        Recensione recensione = new Recensione();
        Mockito.when(recensioneRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(recensione));
        Mockito.when(usefulReviewRepository.findByUserIDAndReviewID(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);
        recensioneService.removeUsefulReview(1, 2);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(recensioneRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneRepository);
        Mockito.verify(usefulReviewRepository, Mockito.times(1)).findByUserIDAndReviewID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usefulReviewRepository);
    }

    @Test
    public void testRemoveUsefulReview() {
        UsersInfo usersInfo = new UsersInfo();
        Mockito.when(usersInfoRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(usersInfo));
        Recensione recensione = new Recensione();
        Mockito.when(recensioneRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(recensione));
        UsefulReview usefulReview = new UsefulReview();
        Mockito.when(usefulReviewRepository.findByUserIDAndReviewID(Mockito.anyInt(), Mockito.anyInt())).thenReturn(usefulReview);
        recensioneService.removeUsefulReview(1, 2);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(recensioneRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(recensioneRepository);
        Mockito.verify(usefulReviewRepository, Mockito.times(1)).findByUserIDAndReviewID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(usefulReviewRepository, Mockito.times(1)).delete(Mockito.any(UsefulReview.class));
        Mockito.verifyNoMoreInteractions(usefulReviewRepository);
    }
}
