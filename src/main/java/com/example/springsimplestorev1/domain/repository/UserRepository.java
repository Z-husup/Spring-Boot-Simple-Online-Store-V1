package com.example.springsimplestorev1.domain.repository;

import com.example.springsimplestorev1.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    boolean existsById(Long id);

    boolean existsByEmail(String email);

    List<User> findAll();
}
