package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.signup.DefaultSignUpClinicOwnerUseCase;
import com.jettech.api.solutions_clinic.model.usecase.signup.DefaultSignUpSoloUseCase;
import com.jettech.api.solutions_clinic.model.usecase.signup.SignUpClinicOwnerRequest;
import com.jettech.api.solutions_clinic.model.usecase.signup.SignUpSoloRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SignUpController implements SignUpAPI {

    private final DefaultSignUpClinicOwnerUseCase signUpClinicOwnerUseCase;
    private final DefaultSignUpSoloUseCase signUpSoloUseCase;

    @Override
    public ResponseEntity<Object> signUpClinicOwner(@Valid @RequestBody SignUpClinicOwnerRequest request) {
        try {
            var response = signUpClinicOwnerUseCase.execute(request);
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao processar cadastro: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Object> signUpSolo(@Valid @RequestBody SignUpSoloRequest request) {
        try {
            var response = signUpSoloUseCase.execute(request);
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao processar cadastro: " + e.getMessage());
        }
    }
}

