package com.example.springsimplestorev1.infrastructure.web;

import com.example.springsimplestorev1.application.usecase.user.GetAllUsersUseCase;
import com.example.springsimplestorev1.application.usecase.user.GetUserByEmailUseCase;
import com.example.springsimplestorev1.application.usecase.user.GetUserByIdUseCase;
import com.example.springsimplestorev1.application.usecase.user.RegisterUserUseCase;
import com.example.springsimplestorev1.application.usecase.user.UpdateUserNameUseCase;
import com.example.springsimplestorev1.domain.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final UpdateUserNameUseCase updateUserNameUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GetUserByEmailUseCase getUserByEmailUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            RegisterUserUseCase registerUserUseCase,
            UpdateUserNameUseCase updateUserNameUseCase,
            GetUserByIdUseCase getUserByIdUseCase,
            GetUserByEmailUseCase getUserByEmailUseCase,
            GetAllUsersUseCase getAllUsersUseCase,
            PasswordEncoder passwordEncoder
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.updateUserNameUseCase = updateUserNameUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.getUserByEmailUseCase = getUserByEmailUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<UserResponse> register(@RequestBody RegisterUserRequest request) {
        User user = registerUserUseCase.execute(
                request.email(),
                request.name(),
                passwordEncoder.encode(request.password())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    @PatchMapping("/{userId}/name")
    public UserResponse updateName(@PathVariable Long userId, @RequestBody UpdateUserNameRequest request) {
        return toResponse(updateUserNameUseCase.execute(userId, request.name()));
    }

    @GetMapping("/{userId}")
    public UserResponse getById(@PathVariable Long userId) {
        return toResponse(getUserByIdUseCase.execute(userId));
    }

    @GetMapping("/by-email")
    public UserResponse getByEmail(@RequestParam String email) {
        return toResponse(getUserByEmailUseCase.execute(email));
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return getAllUsersUseCase.execute().stream().map(this::toResponse).toList();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName());
    }

    public record RegisterUserRequest(String email, String name, String password) {
    }

    public record UpdateUserNameRequest(String name) {
    }

    public record UserResponse(Long id, String email, String name) {
    }
}
