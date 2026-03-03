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

    public User(String email, String name) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email is required");
        }

        this.email = email;
        this.name = name;
    }

    public void changeName(String newName) {
        this.name = newName;
    }
}
