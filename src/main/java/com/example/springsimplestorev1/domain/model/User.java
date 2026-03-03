package com.example.springsimplestorev1.domain.model;

import com.example.springsimplestorev1.domain.exception.ValidationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String name;
    private String passwordHash;

    public User(String email, String name, String passwordHash) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email is required");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new ValidationException("Password hash is required");
        }

        this.email = email;
        this.name = name;
        this.passwordHash = passwordHash;
    }

    public void changeName(String newName) {
        this.name = newName;
    }

    public void changePasswordHash(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new ValidationException("Password hash is required");
        }
        this.passwordHash = newPasswordHash;
    }
}
