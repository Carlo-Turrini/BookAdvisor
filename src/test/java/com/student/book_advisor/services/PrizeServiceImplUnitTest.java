package com.student.book_advisor.services;

import com.student.book_advisor.customExceptions.ApplicationException;
import com.student.book_advisor.data_persistency.model.dto.PrizeDTO;
import com.student.book_advisor.data_persistency.model.dto.formDTOS.PrizeFormDTO;
import com.student.book_advisor.data_persistency.model.entities.Libro;
import com.student.book_advisor.data_persistency.model.entities.Prize;
import com.student.book_advisor.data_persistency.repositories.LibroRepository;
import com.student.book_advisor.data_persistency.repositories.PrizeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
public class PrizeServiceImplUnitTest {
    @TestConfiguration
    static class PrizeServiceImplTestContextConfiguration {
        @Bean
        public PrizeService prizeService() {
            return new PrizeServiceImpl();
        }
    }

    @Autowired
    private PrizeService prizeService;

    @MockBean
    private LibroRepository libroRepository;

    @MockBean
    private PrizeRepository prizeRepository;

    Libro book = null;

    @Before
    public void setup() {
        book = new Libro();
        book.setTitolo("Titolo");
        book.setAnnoPubblicazione(1234);
        book.setPagine(123);
        book.setSinossi("sinossi");
    }

    @Test
    public void testAddPrize_bookPresent() {
        Prize prize = new Prize();
        Mockito.when(libroRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(book));
        Mockito.when(prizeRepository.save(Mockito.any(Prize.class))).thenReturn(prize);
        PrizeFormDTO prizeFormDTO = new PrizeFormDTO();
        prizeFormDTO.setPrizeName("Premio");
        prizeFormDTO.setYearAwarded(2003);
        prizeService.addPrize(prizeFormDTO,1);
        Mockito.verify(libroRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verify(prizeRepository, Mockito.times(1)).save(Mockito.any(Prize.class));
        Mockito.verifyNoMoreInteractions(prizeRepository);
    }

    @Test
    public void testAddPrize_bookNotPresent() {
        PrizeFormDTO prizeFormDTO = new PrizeFormDTO();
        prizeFormDTO.setPrizeName("Premio");
        prizeFormDTO.setYearAwarded(2003);
        assertThatThrownBy(() -> prizeService.addPrize(prizeFormDTO,1))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("This book doesn't exist!");
        Mockito.verify(libroRepository, Mockito.times(1)).findById(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(libroRepository);
        Mockito.verifyNoInteractions(prizeRepository);
    }

    @Test
    public void testDeletePrize_present() {
        Prize prize = new Prize();
        Mockito.when(prizeRepository.findByIdAndBookID(Mockito.anyInt(), Mockito.anyInt())).thenReturn(prize);
        prizeService.deletePrize(1, 2);
        Mockito.verify(prizeRepository, Mockito.times(1)).findByIdAndBookID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verify(prizeRepository, Mockito.times(1)).delete(Mockito.any(Prize.class));
        Mockito.verifyNoMoreInteractions(prizeRepository);
    }

    @Test
    public void testDeletePrize_notPresent() {
        Mockito.when(prizeRepository.findByIdAndBookID(Mockito.anyInt(), Mockito.anyInt())).thenReturn(null);
        prizeService.deletePrize(1, 2);
        Mockito.verify(prizeRepository, Mockito.times(1)).findByIdAndBookID(Mockito.anyInt(), Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(prizeRepository);
    }

    @Test
    public void testGetAllPrizesOfBook() {
        List<PrizeDTO> prizeDTOList = new ArrayList<>();
        PrizeDTO prizeDTO = new PrizeDTO(1, 2003, "Premio");
        prizeDTOList.add(prizeDTO);
        Mockito.when(prizeRepository.findAllPrizesForBook(Mockito.anyInt())).thenReturn(prizeDTOList);
        List<PrizeDTO> found = prizeService.getAllPrizesOfBook(1);
        assertThat(found).isNotEmpty();
        assertThat(found).isEqualTo(prizeDTOList);
        Mockito.verify(prizeRepository, Mockito.times(1)).findAllPrizesForBook(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(prizeRepository);
    }

    @Test
    public void testIsPrizeAlreadyAssignedToBook_True() {
        Prize prize = new Prize();
        Mockito.when(prizeRepository.findByBookIDAndPrizeName(Mockito.anyInt(), Mockito.anyString())).thenReturn(prize);
        Boolean alreadyAssigned = prizeService.isPrizeAlreadyAssignedToBook("Premio", 1);
        assertThat(alreadyAssigned).isTrue();
        Mockito.verify(prizeRepository, Mockito.times(1)).findByBookIDAndPrizeName(Mockito.anyInt(), Mockito.anyString());
        Mockito.verifyNoMoreInteractions(prizeRepository);
    }

    @Test
    public void testIsPrizeAlreadyAssignedToBook_False() {
        Mockito.when(prizeRepository.findByBookIDAndPrizeName(Mockito.anyInt(), Mockito.anyString())).thenReturn(null);
        Boolean alreadyAssigned = prizeService.isPrizeAlreadyAssignedToBook("Premio", 1);
        assertThat(alreadyAssigned).isFalse();
        Mockito.verify(prizeRepository, Mockito.times(1)).findByBookIDAndPrizeName(Mockito.anyInt(), Mockito.anyString());
        Mockito.verifyNoMoreInteractions(prizeRepository);
    }


    @Test
    public void testGetPrizeOfBookFromName() {
        PrizeDTO prizeDTO = new PrizeDTO(1, 2003, "Premio");
        Mockito.when(prizeRepository.findDTOByBookIDAndPrizeName(Mockito.anyInt(), Mockito.anyString())).thenReturn(prizeDTO);
        PrizeDTO found = prizeService.getPrizeOfBookFromName(1, "Premio");
        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(prizeDTO);
        Mockito.verify(prizeRepository, Mockito.times(1)).findDTOByBookIDAndPrizeName(Mockito.anyInt(), Mockito.anyString());
        Mockito.verifyNoMoreInteractions(prizeRepository);
    }
}
