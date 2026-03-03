package com.example.springsimplestorev1.application.usecase.user;

import com.example.springsimplestorev1.domain.exception.DuplicateResourceException;
import com.example.springsimplestorev1.domain.model.User;
import com.example.springsimplestorev1.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUserUseCase {

    private final UserRepository userRepository;

    public RegisterUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User execute(String email, String name, String passwordHash) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("A user with this email already exists");
        }
        return userRepository.save(new User(email, name, passwordHash));
    }
}
