package com.student.book_advisor.repositories;

import com.student.book_advisor.data_persistency.model.dto.UsersInfoDTO;
import com.student.book_advisor.data_persistency.model.dto.UtenteCardDTO;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.UsersInfoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UsersInfoRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UsersInfoRepository usersInfoRepository;

    UsersInfo usersInfo = null;

    @Before
    public void setup() {
        usersInfo = new UsersInfo();
        usersInfo.setUsername("Username");
        usersInfo.setPassword("password");
        usersInfo.setName("Mario");
        usersInfo.setSurname("Rossi");
        usersInfo.setEmail("mario.rossi@mail.it");
        usersInfo.setDescription("Test");
        usersInfo = testEntityManager.persistAndFlush(usersInfo);
    }

    @Test
    public void testFindUserById() {
        UsersInfoDTO usersInfoDTO = usersInfoRepository.findUserById(usersInfo.getId());
        assertThat(usersInfoDTO).isNotNull();
        assertThat(usersInfoDTO.getId()).isEqualTo(usersInfo.getId());
    }

    @Test
    public void testFindAllUsers() {
        List<UtenteCardDTO> utenteCardDTOList = usersInfoRepository.findAllUsers();
        assertThat(utenteCardDTOList).isNotEmpty();
        assertThat(utenteCardDTOList.size()).isEqualTo(1);
        assertThat(utenteCardDTOList.get(0).getId()).isEqualTo(usersInfo.getId());
    }

    @Test
    public void testGetUserProfilePhotoPath() {
        String photoPath = usersInfoRepository.getUserProfilePhotoPath(usersInfo.getId());
        assertThat(photoPath).isNotNull();
        assertThat(photoPath).isEqualTo(usersInfo.getProfilePhotoPath());
    }

    @Test
    public void testCountAllByUsername() {
        Integer count = usersInfoRepository.countAllByUsername(usersInfo.getUsername());
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testCountAllByEmail() {
        Integer count = usersInfoRepository.countAllByEmail(usersInfo.getEmail());
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void testFindByUsername() {
        UsersInfo found = usersInfoRepository.findByUsername(usersInfo.getUsername());
        assertThat(found).isNotNull();
        assertThat(found).isEqualTo(usersInfo);
    }
}
