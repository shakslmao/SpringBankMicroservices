package devshaks.bank_microservices.auth;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    public void register(RegistrationRequest registrationRequest) throws MessagingException {}

}
