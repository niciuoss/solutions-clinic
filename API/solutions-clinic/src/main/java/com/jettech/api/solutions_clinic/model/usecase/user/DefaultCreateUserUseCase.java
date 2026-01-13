package com.jettech.api.solutions_clinic.model.usecase.user;

import com.jettech.api.solutions_clinic.model.entity.User;
import com.jettech.api.solutions_clinic.model.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DefaultCreateUserUseCase implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User execute(CreateUserRequest in) {
        // Validar email duplicado
        userRepository
                .findByEmail(in.email())
                .ifPresent((user) -> {
                    throw new RuntimeException("Email already exists: " + in.email());
                });

        // Validar CPF duplicado (se informado)
        if (in.cpf() != null && !in.cpf().trim().isEmpty()) {
            userRepository
                    .findByCpf(in.cpf())
                    .ifPresent((user) -> {
                        throw new RuntimeException("CPF já está cadastrado: " + in.cpf());
                    });
        }

        var password = this.passwordEncoder.encode(in.password());

        final User user = new User();
        user.setFirstName(in.firstName());
        user.setLastName(in.lastName());
        user.setEmail(in.email());
        user.setPassword(password);
        
        // Campos opcionais
        if (in.phone() != null && !in.phone().trim().isEmpty()) {
            user.setPhone(in.phone());
        }
        if (in.cpf() != null && !in.cpf().trim().isEmpty()) {
            user.setCpf(in.cpf());
        }
        if (in.birthDate() != null && !in.birthDate().trim().isEmpty()) {
            user.setBirthDate(in.birthDate());
        }

        userRepository.save(user);

        return user;
    }

}
