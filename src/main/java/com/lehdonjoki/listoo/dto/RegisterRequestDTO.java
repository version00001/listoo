package com.lehdonjoki.listoo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @Schema(description = "User's email address", example = "user@example.com")
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "User's password", example = "P@ssw0rd!")
    @NotBlank(message = "Password is required")
    private String password;

    @Schema(description = "User's full name", example = "John Doe")
    @NotBlank(message = "Name is required")
    private String name;
}
