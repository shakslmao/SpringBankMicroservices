package devshaks.bank_microservices.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Request class for user registration.
 */
@Getter
@Setter
@Builder
public class RegistrationRequest {

    // First name of the user, must not be empty or blank
    @NotEmpty(message = "First Name is Required")
    @NotBlank(message = "First Name is Required")
    private String firstName;

    // Last name of the user, must not be empty or blank
    @NotEmpty(message = "Last Name is Required")
    @NotBlank(message = "Last Name is Required")
    private String lastName;

    // Email of the user, must not be empty, blank, and must be a valid email format
    @NotEmpty(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    // Password of the user, must not be empty, blank, and must be at least 8 characters long
    @NotEmpty(message = "Password is required")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    // User PIN, must not be empty, blank, and must be exactly 4 digits long
    @NotEmpty(message = "Pin is required")
    @NotBlank(message = "Pin is required")
    @Size(min = 4, max = 4, message = "Pin must be 4 digits")
    private String userPin;
}
