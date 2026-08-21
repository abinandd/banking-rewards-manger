package com.example.rewards.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "User registration, profile management, and loyalty tier endpoints")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Data
    public static class CreateUserRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @Email(message = "Valid email is required")
        @NotBlank(message = "Email is required")
        private String email;

        private UserTier tier;
    }

    @Data
    public static class UpdateTierRequest {
        private UserTier tier;
    }

    @Operation(summary = "Register user", description = "Create a new banking customer with referral code and initial reward wallet")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Email already exists or invalid data")
    })
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.registerUser(request.getName(), request.getEmail(), request.getTier());
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @Operation(summary = "Get user by ID", description = "Retrieve customer profile by their unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "List all users", description = "Retrieve list of all active banking users")
    @ApiResponse(responseCode = "200", description = "List of users")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Update user loyalty tier", description = "Upgrade or downgrade customer tier (SILVER, GOLD, PLATINUM)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tier updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/tier")
    public ResponseEntity<User> updateTier(@PathVariable Long id, @RequestBody UpdateTierRequest request) {
        return ResponseEntity.ok(userService.updateUserTier(id, request.getTier()));
    }
}
