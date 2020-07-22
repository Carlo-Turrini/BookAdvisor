package com.student.book_advisor.repositories;

import com.student.book_advisor.data_persistency.model.entities.Authorities;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.AuthoritiesRepository;
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
public class AuthoritiesRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    Authorities authority = null;
    UsersInfo user = null;

    @Before
    public void setup() {
        authority = new Authorities();
        authority.setAuthority("USER");
        user = new UsersInfo();
        user.setDescription("prova");
        user.setName("Albert");
        user.setSurname("Einstein");
        user.setEmail("a.e@prova.it");
        user.setPassword("password");
        user.setUsername("AlEi");
        user = testEntityManager.persist(user);
        authority.setUsersInfo(user);
        testEntityManager.persist(authority);
        testEntityManager.flush();
    }

    @Test
    public void testFindAllByUserId() {

        List<Authorities> authorities = authoritiesRepository.findAllByUserID(user.getId());
        assertThat(authorities.size()).isEqualTo(1);
        assertThat(authorities.get(0).getAuthority()).isEqualTo("USER");
        assertThat(authorities.contains(authority)).isTrue();

    }

    @Test
    public void testNotFoundAuthoritiesByUserId() {
        List<Authorities> authorities = authoritiesRepository.findAllByUserID(4);
        assertThat(authorities).isEmpty();
    }
}
