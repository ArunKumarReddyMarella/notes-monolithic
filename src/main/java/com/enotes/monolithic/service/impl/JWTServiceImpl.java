package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.entity.Role;
import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.exception.JwtTokenExpiredException;
import com.enotes.monolithic.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTServiceImpl implements JWTService {

    private String SECRET_KEY = "DefaultSecretKey";

    public JWTServiceImpl() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGenerator.generateKey();
            SECRET_KEY = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
//            log.error("JWTServiceImpl :: JWTServiceImpl ::", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("id", user.getId());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles().stream().map(Role::getName).toList());
        claims.put("AccountStatus", user.getAccountStatus().getIsActive());

        long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 1 day
        return Jwts.builder()
                .claims().add(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .and()
                .signWith(getSecretKey())
                .compact();
    }

    @Override
    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    @Override
    public Boolean extractAccountStatus(String token) {
        Claims claims = extractAllClaims(token);
        return (Boolean)claims.get("AccountStatus");
    }

    @Override
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles").toString();
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(decryptKey(SECRET_KEY)).build()
                    .parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtTokenExpiredException("Token Expired");
        }
        catch (JwtException e) {
            throw new JwtTokenExpiredException("Invalid Token");
        }
        catch (Exception e) {
            throw e;
        }
    }

    private SecretKey decryptKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        Boolean isExpired = isTokenExpired(token);
        return (username.equals(userDetails.getUsername()) && !isExpired && extractAccountStatus(token));
    }

    private Key getSecretKey() {
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            return new SecretKeySpec(decoder.decode(SECRET_KEY), "HmacSHA256");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

//        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
//        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Boolean isTokenExpired(String token) {
        Claims claims = extractAllClaims(token);
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }
}
