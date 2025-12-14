package com.jettech.api.solutions_clinic.model.usecase.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.jettech.api.solutions_clinic.model.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultAuthUserUseCase implements AuthUserUseCase {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.token.secret}")
    private String secretKey;

    @Value("${security.jwt.issuer:solutions-clinic}")
    private String issuer;

    @Override
    public AuthUserResponse execute(AuthUserRequest authUserRequest) throws AuthenticationException {
        var user = this.userRepository.findByEmail(authUserRequest.email()).orElseThrow(() -> {
            throw new UsernameNotFoundException("Username/password invalido");
        });

        var passwordMatches = passwordEncoder.matches(authUserRequest.password(), user.getPassword());

        if(!passwordMatches) {
            throw new AuthenticationException();
        }

        var expiresIn = Instant.now().plus(Duration.ofDays(1));
        Algorithm  algorithm = Algorithm.HMAC256(secretKey);
        var token = JWT.create()
                .withIssuer(issuer)
                .withExpiresAt(expiresIn)
                .withSubject(user.getId().toString())
                .sign(algorithm);

        return new AuthUserResponse(token, expiresIn.toEpochMilli());

    }
}
