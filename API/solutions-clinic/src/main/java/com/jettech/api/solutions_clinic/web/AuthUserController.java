package com.jettech.api.solutions_clinic.web;


import com.jettech.api.solutions_clinic.model.usecase.user.AuthUserRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.AuthUserResponse;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultAuthUserUseCase;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import javax.naming.AuthenticationException;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AuthUserController implements AuthUserAPI {


    private final DefaultAuthUserUseCase defaultAuthUserUseCase;


    @Override
    public AuthUserResponse signIn(@Valid @RequestBody AuthUserRequest authUserRequest) throws AuthenticationException {
        return defaultAuthUserUseCase.execute(authUserRequest);
    }
}
