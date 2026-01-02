package com.project.userservice.dto;

import com.project.userservice.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class RegisterRequest {
    @NotBlank(message="Email is required")
    @Email(message="Invalid email format")
    private String email;
    @NotBlank(message="password is required")
    @Size(min = 6,message="Password mus have atleast 6 characters")
    private String password;
    private String firstName;
    private String lastName;
    private UserRole role;
}
