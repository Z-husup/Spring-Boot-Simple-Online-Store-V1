package com.example.springsimplestorev1.application.usecase.user;

import com.example.springsimplestorev1.domain.exception.ResourceNotFoundException;
import com.example.springsimplestorev1.domain.model.User;
import com.example.springsimplestorev1.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class GetUserByEmailUseCase {

    private final UserRepository userRepository;

    public GetUserByEmailUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
    }
}