package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.signup.DefaultSignUpClinicOwnerUseCase;
import com.jettech.api.solutions_clinic.model.usecase.signup.DefaultSignUpSoloUseCase;
import com.jettech.api.solutions_clinic.model.usecase.signup.SignUpClinicOwnerRequest;
import com.jettech.api.solutions_clinic.model.usecase.signup.SignUpResponse;
import com.jettech.api.solutions_clinic.model.usecase.signup.SignUpSoloRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SignUpController implements SignUpAPI {

    private final DefaultSignUpClinicOwnerUseCase signUpClinicOwnerUseCase;
    private final DefaultSignUpSoloUseCase signUpSoloUseCase;

    @Override
    public SignUpResponse signUpClinicOwner(@Valid @RequestBody SignUpClinicOwnerRequest request) throws AuthenticationException {
        return signUpClinicOwnerUseCase.execute(request);
    }

    @Override
    public SignUpResponse signUpSolo(@Valid @RequestBody SignUpSoloRequest request) throws AuthenticationException {
        return signUpSoloUseCase.execute(request);
    }
}

