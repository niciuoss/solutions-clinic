package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.user.CreateUserRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultCreateUserUseCase;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultGetUserByIdUseCase;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserController implements UserAPI {

    private final DefaultCreateUserUseCase defaultCreateUserUseCase;
    private final DefaultGetUserByIdUseCase getUserByIdUseCase;

    @Override
    public ResponseEntity<Object> createUser(@Valid @RequestBody CreateUserRequest in) {
        var result = this.defaultCreateUserUseCase.execute(in);
        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseEntity<Object> getUserById(@PathVariable UUID id) {
        try {
            var result = getUserByIdUseCase.execute(id);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar usuário: " + e.getMessage());
        }
    }
}
