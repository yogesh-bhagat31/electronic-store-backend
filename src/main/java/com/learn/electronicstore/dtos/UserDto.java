package com.learn.electronicstore.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learn.electronicstore.enums.Providers;
import com.learn.electronicstore.validate.ImageNameValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private String userId;

    @Size(min = 3, max = 10, message = "Invalid Name")
    private String name;

    @Email(message = "Invalid Mail Id")
    private String email;

    @JsonIgnore
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one number, and one special character")
    private String password;

    @Size(min = 4, max = 6, message = "Invalid Gender")
    private String gender;

    @NotBlank(message = "Write something about yourself")
    private String about;

    @ImageNameValid
    private String imageName;

    private Providers provider;

    List<RoleDto> roles;
}

