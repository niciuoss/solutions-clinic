package com.jettech.api.solutions_clinic.web;


import com.jettech.api.solutions_clinic.model.usecase.user.AuthUserRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultAuthUserUseCase;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AuthUserController implements AuthUserAPI {


    private final DefaultAuthUserUseCase defaultAuthUserUseCase;


    @Override
    public ResponseEntity<Object> signIn(@Valid @RequestBody AuthUserRequest authUserRequest) {
        try {
            var token = this.defaultAuthUserUseCase.execute(authUserRequest);
            return ResponseEntity.ok().body(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
