package com.example.springsimplestorev1.infrastructure.persistence.repository;

import com.example.springsimplestorev1.domain.model.User;
import com.example.springsimplestorev1.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {
}
