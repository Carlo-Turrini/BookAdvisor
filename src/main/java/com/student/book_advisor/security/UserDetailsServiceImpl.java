package com.student.book_advisor.security;

import com.student.book_advisor.entities.UsersInfo;
import com.student.book_advisor.entityRepositories.AuthoritiesRepository;
import com.student.book_advisor.entityRepositories.UsersInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UsersInfoRepository usersInfoRepository;

    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersInfo user = usersInfoRepository.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new AuthUserPrincipal(user, getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(UsersInfo user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authoritiesRepository.findAllByUserID(user.getId()).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getAuthority()));
        });
        return authorities;
    }
}
