package com.student.book_advisor.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Resource(name = "userDetailsService")
    UserDetailsService userDetailsService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    LoginSuccessHandler loginSuccessHandler;

    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().addFilterBefore(new StatelessCsrfFilter(), CsrfFilter.class)
                /*.formLogin(form -> form
                        .loginProcessingUrl("/login").permitAll()
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler))*/
                .headers(headers -> headers.disable())
                .authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "/authenticate").permitAll()
                .mvcMatchers(HttpMethod.GET, "/authors").permitAll()
                .mvcMatchers(HttpMethod.POST, "/utenti").permitAll()
                .mvcMatchers(HttpMethod.GET, "/utenti/{id}").permitAll()
                .mvcMatchers(HttpMethod.POST, "/utenti/isUsernameUnique").permitAll()
                .mvcMatchers(HttpMethod.POST, "/utenti/isEmailUnique").permitAll()
                .mvcMatchers(HttpMethod.GET, "/utenti/{id}/myBooks").permitAll()
                .mvcMatchers(HttpMethod.GET, "/utenti/{id}/bookRank").permitAll()
                .mvcMatchers(HttpMethod.GET, "/libri").permitAll()
                .mvcMatchers(HttpMethod.GET, "/libri/{id}").permitAll()
                .mvcMatchers(HttpMethod.GET, "/libri/{id}/overallRatings").permitAll()
                .mvcMatchers(HttpMethod.GET,"/libri/{id}/prizes").permitAll()
                .mvcMatchers(HttpMethod.GET, "/libri/{id}/recensioni").permitAll()
                .mvcMatchers(HttpMethod.GET, "/utenti/{id}/recensioni").permitAll()
                .mvcMatchers(HttpMethod.GET, "/authors/{id}").permitAll()
                .mvcMatchers(HttpMethod.GET, "/genres").permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .and()//.addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtTokenProvider))
                .addFilter(new JwtTokenFilter(authenticationManager(), jwtTokenProvider))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().logout(logout -> logout
                .logoutUrl("/logout")
                .addLogoutHandler(new CustomLogoutHandler(jwtTokenProvider)));
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }*/
}
