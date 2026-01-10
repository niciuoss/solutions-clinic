package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.user.CreateUserRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultCreateUserUseCase;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultGetUserByIdUseCase;
import com.jettech.api.solutions_clinic.model.entity.User;
import com.jettech.api.solutions_clinic.model.usecase.user.UserDetailResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserController implements UserAPI {

    private final DefaultCreateUserUseCase defaultCreateUserUseCase;
    private final DefaultGetUserByIdUseCase getUserByIdUseCase;

    @Override
    public User createUser(@Valid @RequestBody CreateUserRequest in) throws AuthenticationException {
        return defaultCreateUserUseCase.execute(in);
    }

    @Override
    public UserDetailResponse getUserById(@PathVariable UUID id) throws AuthenticationException {
        return getUserByIdUseCase.execute(id);
    }
}
