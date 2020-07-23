package com.student.book_advisor.controllers;

import com.student.book_advisor.data_persistency.model.dto.LoginDTO;
import com.student.book_advisor.security.AuthUserPrincipal;
import com.student.book_advisor.security.CookieUtil;
import com.student.book_advisor.security.JwtProperties;
import com.student.book_advisor.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/authenticate")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAuth(@RequestBody()LoginDTO logInfo, HttpServletResponse response) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logInfo.getUsername(), logInfo.getPassword()));
            AuthUserPrincipal authUserPrincipal = (AuthUserPrincipal) userDetailsService.loadUserByUsername(logInfo.getUsername());
            String jwtToken = jwtTokenProvider.createToken(authUserPrincipal.getUsername(), authUserPrincipal.getId(), authUserPrincipal.getAuthoritiesToString());
            CookieUtil.create(response, JwtProperties.JWT_TOKEN_COOKIE, jwtToken, false, true, (int) JwtProperties.JWT_EXPIRATION/1000);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (DisabledException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", e);
        }
    }

    @GetMapping("/logoutUser")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            SecurityContextHolder.getContext().setAuthentication(null);
            jwtTokenProvider.invalidateJwtToken(request, response);
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Logout error", e);
        }
    }
}
