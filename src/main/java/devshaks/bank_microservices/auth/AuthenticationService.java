package devshaks.bank_microservices.auth;
import devshaks.bank_microservices.roles.ERoles;
import devshaks.bank_microservices.roles.RoleRepository;
import devshaks.bank_microservices.user.User;
import devshaks.bank_microservices.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegistrationRequest registrationRequest) throws MessagingException {
        var userRole = roleRepository.findByName(ERoles.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("Role Not Found"));

        var user = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .address(registrationRequest.getAddress())
                .userPin(registrationRequest.getUserPin())
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
    }


}
