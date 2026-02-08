package com.jettech.api.solutions_clinic.web;


import com.jettech.api.solutions_clinic.model.usecase.user.AuthUserRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.AuthUserResponse;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultAuthUserUseCase;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultSwitchTenantUseCase;
import com.jettech.api.solutions_clinic.model.usecase.user.SwitchTenantRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AuthUserController implements AuthUserAPI {

    private final DefaultAuthUserUseCase defaultAuthUserUseCase;
    private final DefaultSwitchTenantUseCase defaultSwitchTenantUseCase;

    @Override
    public AuthUserResponse signIn(@Valid @RequestBody AuthUserRequest authUserRequest) throws AuthenticationFailedException {
        return defaultAuthUserUseCase.execute(authUserRequest);
    }

    @Override
    public AuthUserResponse switchTenant(@Valid @RequestBody SwitchTenantRequest request) throws AuthenticationFailedException {
        return defaultSwitchTenantUseCase.execute(request);
    }
}
