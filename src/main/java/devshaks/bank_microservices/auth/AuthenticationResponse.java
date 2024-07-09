package devshaks.bank_microservices.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
/**
 * Response class for authentication.
 */
public class AuthenticationResponse {
    private String token;
}
