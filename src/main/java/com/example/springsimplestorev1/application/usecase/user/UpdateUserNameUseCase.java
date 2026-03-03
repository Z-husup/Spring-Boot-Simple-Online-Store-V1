package com.example.springsimplestorev1.application.usecase.user;

import com.example.springsimplestorev1.domain.exception.ResourceNotFoundException;
import com.example.springsimplestorev1.domain.model.User;
import com.example.springsimplestorev1.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateUserNameUseCase {

    private final UserRepository userRepository;

    public UpdateUserNameUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User execute(Long userId, String newName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.changeName(newName);
        return user;
    }
}