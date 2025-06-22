package com.enotes.monolithic.service;

import com.enotes.monolithic.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
    public String generateToken(User user);

    public String extractUsername(String token);

    public Boolean extractAccountStatus(String token);

    public String extractRole(String token);

    public boolean validateToken(String token, UserDetails userDetails);
}
