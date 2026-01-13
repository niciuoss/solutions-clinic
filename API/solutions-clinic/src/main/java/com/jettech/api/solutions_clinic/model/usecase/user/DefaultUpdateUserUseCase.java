package com.jettech.api.solutions_clinic.model.usecase.user;

import com.jettech.api.solutions_clinic.model.entity.User;
import com.jettech.api.solutions_clinic.model.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultUpdateUserUseCase implements UpdateUserUseCase {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse execute(UpdateUserRequest request) throws AuthenticationException {
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + request.id()));

        // Atualizar campos se fornecidos
        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.cpf() != null) {
            user.setCpf(request.cpf());
        }
        if (request.birthDate() != null) {
            user.setBirthDate(request.birthDate());
        }
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            // Verificar se o email já existe
            final User finalUser = user;
            userRepository.findByEmail(request.email())
                    .ifPresent((existingUser) -> {
                        if (!existingUser.getId().equals(finalUser.getId())) {
                            throw new RuntimeException("Email já está em uso: " + request.email());
                        }
                    });
            user.setEmail(request.email());
        }

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getCpf(),
                savedUser.getBirthDate(),
                savedUser.isBlocked(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt()
        );
    }
}
