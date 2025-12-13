package com.api.clinica.solutions_clinic.providers;

import com.api.clinica.solutions_clinic.exception.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JWTProvider {

    @Value("${security.token.secret}")
    private String secretKey;

    public String validateToken(String token) {
        token = token.replace("Bearer ", "");

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        try {
            return JWT.require(algorithm).build().verify(token).getSubject();
        }catch (JWTVerificationException e) {
            e.printStackTrace();
            return "";
        }
    }
}
