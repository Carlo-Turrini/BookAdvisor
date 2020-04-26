package com.student.book_advisor.controllers;


import com.student.book_advisor.dto.LoginDTO;
import com.student.book_advisor.security.AuthToken;
import com.student.book_advisor.security.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody() LoginDTO loginInfo) throws AuthenticationException {
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginInfo.getUsername(), loginInfo.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String jwtToken = jwtTokenUtil.generateToken(authentication);
        return ResponseEntity.ok(new AuthToken(jwtToken));
    }
}
