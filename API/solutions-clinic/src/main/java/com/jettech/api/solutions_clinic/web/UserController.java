package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.user.CreateUserRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultCreateUserUseCase;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserController implements UserAPI {

    private final DefaultCreateUserUseCase defaultCreateUserUseCase;

    @Override
    public ResponseEntity<Object> createUser(@Valid @RequestBody CreateUserRequest in) {
        var result = this.defaultCreateUserUseCase.execute(in);
        return ResponseEntity.ok().body(result);
    }
}
