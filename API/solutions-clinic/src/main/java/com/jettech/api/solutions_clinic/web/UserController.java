package com.jettech.api.solutions_clinic.web;

import com.jettech.api.solutions_clinic.model.usecase.user.CreateUserRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultCreateUserUseCase;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultDeleteUserUseCase;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultGetUserByIdUseCase;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultGetUsersByTenantUseCase;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultUpdateUserBlockedUseCase;
import com.jettech.api.solutions_clinic.model.usecase.user.DefaultUpdateUserUseCase;
import com.jettech.api.solutions_clinic.model.usecase.user.GetUsersByTenantRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.UpdateUserBlockedRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.UpdateUserBlockedBodyRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.UpdateUserBodyRequest;
import com.jettech.api.solutions_clinic.model.usecase.user.UpdateUserRequest;
import com.jettech.api.solutions_clinic.model.entity.User;
import com.jettech.api.solutions_clinic.model.usecase.user.UserDetailResponse;
import com.jettech.api.solutions_clinic.model.usecase.user.UserResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jettech.api.solutions_clinic.exception.AuthenticationFailedException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class UserController implements UserAPI {

    private final DefaultCreateUserUseCase defaultCreateUserUseCase;
    private final DefaultGetUserByIdUseCase getUserByIdUseCase;
    private final DefaultGetUsersByTenantUseCase getUsersByTenantUseCase;
    private final DefaultUpdateUserUseCase updateUserUseCase;
    private final DefaultDeleteUserUseCase deleteUserUseCase;
    private final DefaultUpdateUserBlockedUseCase updateUserBlockedUseCase;

    @Override
    public User createUser(@Valid @RequestBody CreateUserRequest in) throws AuthenticationFailedException {
        return defaultCreateUserUseCase.execute(in);
    }

    @Override
    public Page<UserResponse> getUsersByTenant(
            @RequestParam UUID tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false, defaultValue = "firstName,asc") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean blocked,
            @RequestParam(required = false) com.jettech.api.solutions_clinic.model.entity.Role role
    ) throws AuthenticationFailedException {
        return getUsersByTenantUseCase.execute(new GetUsersByTenantRequest(
                tenantId,
                page,
                size,
                sort,
                search,
                blocked,
                role
        ));
    }

    @Override
    public UserDetailResponse getUserById(@PathVariable UUID id) throws AuthenticationFailedException {
        return getUserByIdUseCase.execute(id);
    }

    @Override
    public UserResponse updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserBodyRequest request
    ) throws AuthenticationFailedException {
        return updateUserUseCase.execute(new UpdateUserRequest(
                id,
                request.firstName(),
                request.lastName(),
                request.phone(),
                request.cpf(),
                request.birthDate(),
                request.email()
        ));
    }

    @Override
    public void deleteUser(@PathVariable UUID id) throws AuthenticationFailedException {
        deleteUserUseCase.execute(id);
    }

    @Override
    public UserResponse updateUserBlocked(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserBlockedBodyRequest request
    ) throws AuthenticationFailedException {
        return updateUserBlockedUseCase.execute(new UpdateUserBlockedRequest(
                id,
                request.blocked()
        ));
    }

    @Override
    public Map<String, Boolean> checkCpfExists(@PathVariable String cpf) throws AuthenticationFailedException {
        boolean exists = ((DefaultUpdateUserUseCase) updateUserUseCase).checkCpfExists(cpf);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }
}
