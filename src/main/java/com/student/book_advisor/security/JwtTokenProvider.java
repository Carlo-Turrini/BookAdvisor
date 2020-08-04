package com.student.book_advisor.security;

import com.student.book_advisor.security.redis.RedisUtil;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Component
public class JwtTokenProvider {
    private String secret = JwtProperties.JWT_SECRET;
    private Long validityInMillis = JwtProperties.JWT_EXPIRATION;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String username, Integer userID, List<String> roles) {
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
                    System.out.println("IsMember");
                    return false;
                }
                else return true;
            }
        }
        catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("Expired or invalid JWT token: " + e.getMessage());
        }
    }

    public void invalidateJwtToken(HttpServletRequest request, HttpServletResponse response) {
        String token = resolveToken(request);
        if(token != null) {
            CookieUtil.clear(request, response, JwtProperties.JWT_TOKEN_COOKIE);
            if(validateToken(token)) {
                Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
                String jwtTokenID = claims.getBody().getId();
                Date expirationDate = claims.getBody().getExpiration();
                redisUtil.set(jwtTokenID, token, expirationDate);
            }
        }
    }
}
