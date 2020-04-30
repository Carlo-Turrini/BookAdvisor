package com.student.book_advisor.security;

import com.student.book_advisor.redis.RedisUtil;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CookieValue;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;


//FAI IMPLEMENTAZIONE JWT CON COOKIE E IMPLEMENTAZIONE CSRF CON COOKIE GENERATO LATO CLIENT E MANDATO CON OGNI REQUEST
//ANCHE IN UN HEADER DI MODO CHE IL SERVER VERIFICHI L'UGUAGLIANZA TRA COOKIE E HEADER! IL COOKIE JWT DEVE ESSERE HTTPONLY
//ANCHE SECURE SE DECIDIAMO DI USARE HTTPS... OTTENUTO IL JWT QUANDO L'UTENTE FA LOGIN, LATO CLIENT RICHIEDIAMO USERID,
// E ROLES DEL LOGGED USER TRAMITE UN ENDPOINT APPOSITO, PER LA VISUALIZZAZIONE CORRETTA LATO CLIENT.
//I JWT COOKIE QUANDO SI FA LOGOUT VENGONO ELIMINATI E IN PIù SALVIAMO IN REDIS I TOKEN CHE ANCORA DEVONO EXPIRE IL CUI
//UTENTE POSSESSORE HA GIà FATTO LOGOUT
//IN REDIS AGGIUNGI COME CHIAVE PER UN JWT token che deve ancora expire: l'ID del token, da generare con un UUID, come valore
//mettiamo il token stesso oppure un parametro significativo tipo l'id dello user...
//Visto che jwt sarà un cookie read_only le info sui roles e l'id forse non servono..
@Component
public class JwtTokenProvider {
    private String secret = JwtProperties.JWT_SECRET;
    private Long validityInMillis = JwtProperties.JWT_EXPIRATION;
    private static final String REDIS_ACTIVE_SUBJECTS_ID = "active_subjects";

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String username, Long userID, List<String> roles) {
        UUID jwtTokenID = UUID.randomUUID();
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", roles);
        claims.put("userID", userID);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMillis);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .setId(jwtTokenID.toString())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        AuthUserPrincipal authUserPrincipal = (AuthUserPrincipal) this.userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(authUserPrincipal,"", authUserPrincipal.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public Date getExpiration(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration();
    }

    public String resolveToken(HttpServletRequest request) {
        return CookieUtil.getValue(request, JwtProperties.JWT_TOKEN_COOKIE);
    }

    public Boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            if(claimsJws.getBody().getExpiration().before(new Date())) {
                return false;
            }
            else {
                String jwtTokenID = claimsJws.getBody().getId();
                if(redisUtil.isMember(jwtTokenID, token)) {
                    return false;
                }
                else return true;
            }
        }
        catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("Expired or invalid JWT token");
        }
    }

    public void invalidateJwtToken(HttpServletRequest request, HttpServletResponse response) {
        String token = resolveToken(request);
        if(token != null) {
            CookieUtil.clear(response, JwtProperties.JWT_TOKEN_COOKIE);
            if(validateToken(token)) {
                Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
                String jwtTokenID = claims.getBody().getId();
                Date expirationDate = claims.getBody().getExpiration();
                redisUtil.set(jwtTokenID, token, expirationDate);
            }
        }
    }
}
