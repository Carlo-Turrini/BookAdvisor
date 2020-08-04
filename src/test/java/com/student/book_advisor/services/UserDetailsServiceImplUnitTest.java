package com.student.book_advisor.services;

import com.student.book_advisor.data_persistency.model.entities.Authorities;
import com.student.book_advisor.data_persistency.model.entities.UsersInfo;
import com.student.book_advisor.data_persistency.repositories.AuthoritiesRepository;
import com.student.book_advisor.data_persistency.repositories.UsersInfoRepository;
import com.student.book_advisor.security.AuthUserPrincipal;
import com.student.book_advisor.security.UserDetailsServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
public class UserDetailsServiceImplUnitTest {
    @TestConfiguration
    static class UserDetailsImplTestContextConfiguration {
        @Bean
        public UserDetailsService userDetailsService() {
            return new UserDetailsServiceImpl();
        }
    }

    @Autowired
    private UserDetailsService userDetailsService;

    @MockBean
    private UsersInfoRepository usersInfoRepository;

    @MockBean
    private AuthoritiesRepository authoritiesRepository;


    @Test
    public void testLoadUserByUsername_userNull() {
        Mockito.when(usersInfoRepository.findByUsername(Mockito.anyString())).thenReturn(null);
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("Pablo"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Invalid username or password.");
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findByUsername(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verifyNoInteractions(authoritiesRepository);
    }

    @Test
    public void testLoadUserByUsername_userNotNull() {
        UsersInfo usersInfo = new UsersInfo();
        usersInfo.setId(1);
        Mockito.when(usersInfoRepository.findByUsername(Mockito.anyString())).thenReturn(usersInfo);
        Authorities authorities = new Authorities();
        authorities.setUsersInfo(usersInfo);
        authorities.setAuthority("USER");
        List<Authorities> authoritiesList = new ArrayList<>();
        authoritiesList.add(authorities);
        Mockito.when(authoritiesRepository.findAllByUserID(Mockito.anyInt())).thenReturn(authoritiesList);
        AuthUserPrincipal found = (AuthUserPrincipal) userDetailsService.loadUserByUsername("Pablo");
        assertThat(found).isNotNull();
        assertThat(found.getAuthoritiesToString().contains("ROLE_USER")).isTrue();
        assertThat(found.getUsersInfo()).isEqualTo(usersInfo);
        Mockito.verify(usersInfoRepository, Mockito.times(1)).findByUsername(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(usersInfoRepository);
        Mockito.verify(authoritiesRepository, Mockito.times(1)).findAllByUserID(Mockito.anyInt());
        Mockito.verifyNoMoreInteractions(authoritiesRepository);
    }

}
