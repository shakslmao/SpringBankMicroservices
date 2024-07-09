package devshaks.bank_microservices.auth;

import devshaks.bank_microservices.security.JwtService;
import devshaks.bank_microservices.email.EmailService;
import devshaks.bank_microservices.email.EmailTemplateName;
import devshaks.bank_microservices.roles.ERoles;
import devshaks.bank_microservices.roles.RoleRepository;
import devshaks.bank_microservices.user.Token;
import devshaks.bank_microservices.user.TokenRepository;
import devshaks.bank_microservices.user.User;
import devshaks.bank_microservices.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * Service class for handling user authentication and registration processes.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationURL;

    /**
     * Registers a new user based on the provided registration request.
     *
     * @param registrationRequest The registration request containing user details
     * @throws MessagingException If there is an error sending the validation email
     */
    public void register(RegistrationRequest registrationRequest) throws MessagingException {
        // Retrieve the 'ROLE_USER' role or throw an exception if not found
        var userRole = roleRepository.findByName(ERoles.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("Role Not Found"));

        // Create a new User entity with encoded password and PIN
        var user = User.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .userPin(passwordEncoder.encode(registrationRequest.getUserPin()))
                .address(registrationRequest.getAddress())
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        // Save the user to the database
        userRepository.save(user);

        // Send validation email to the newly registered user
        sendValidationEmail(user);
    }

    /**
     * Sends a validation email to the specified user.
     *
     * @param user The user to whom the validation email will be sent
     * @throws MessagingException If there is an error sending the email
     */
    private void sendValidationEmail(User user) throws MessagingException {
        // Generate a new activation token for the user and save it
        var newToken = generateAndSaveActivationToken(user);

        // Send the validation email using EmailService
        emailService.sendValidationEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_USER_ACCOUNT,
                activationURL,
                newToken,
                "Activate Your Account");
    }

    /**
     * Generates and saves an activation token for the specified user.
     *
     * @param user The user for whom the token will be generated
     * @return The generated activation token
     */
    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationToken(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdTokenAt(LocalDateTime.now())
                .expiredTokenAt(LocalDateTime.now().plusMinutes(20))
                .user(user)
                .build();
        // Save the token to the database
        tokenRepository.save(token);
        return generatedToken;
    }

    /**
     * Generates a random activation token with the specified length.
     *
     * @param length The length of the token to generate
     * @return The generated activation token
     */
    private String generateActivationToken(int length) {
        String tokenChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder tokenCodeBuilder = new StringBuilder();
        SecureRandom secureRandomToken = new SecureRandom();

        for (int i = 0; i < length; i++ ) {
            int randomIndex = secureRandomToken.nextInt(tokenChars.length());
            tokenCodeBuilder.append(tokenChars.charAt(randomIndex));
        }

        return tokenCodeBuilder.toString();
    }

    /**
     * Authenticates the user based on the provided authentication request and generates a JWT token.
     *
     * @param request The authentication request containing either a password or a PIN
     * @return An AuthenticationResponse containing the generated JWT token
     * @throws IllegalArgumentException If neither a password nor a PIN is provided in the request
     */
    public AuthenticationResponse authenticationResponse(AuthenticationRequest request) {
        // Determine the authentication credentials from the request
        String authCredentials;
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            authCredentials = request.getPassword();
        } else if (request.getPin() != null && !request.getPin().isEmpty()) {
            authCredentials = request.getPin();
        } else {
            throw new IllegalArgumentException("Either Pin or Password must be provided");
        }

        // Authenticate the user with the provided email and credentials
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), authCredentials));

        // Prepare the claims to be included in the JWT token
        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.getFullName());

        // Generate the JWT token with the claims and authenticated user
        var jwtToken = jwtService.generateToken(claims, user);

        // Return the authentication response containing the JWT token
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    /**
     * Activates a user account using the provided token.
     *
     * @param token the activation token
     * @throws MessagingException if there is an error sending the validation email
     */
    @Transactional
    public void activateAccount(String token) throws MessagingException {
        // Retrieve the token from the repository or throw an exception if it doesn't exist
        Token savedUserToken = tokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid Token"));

        // Check if the token has expired
        if (LocalDateTime.now().isAfter(savedUserToken.getExpiredTokenAt())) {
            // If the token is expired, send a new validation email and throw an exception
            sendValidationEmail(savedUserToken.getUser());
            throw new RuntimeException("Activation Token has Expired! A new token has been sent to your email");
        }

        // Retrieve the user associated with the token or throw an exception if the user is not found
        var user = userRepository.findById(savedUserToken.getUser().getId())
            .orElseThrow(() -> new UsernameNotFoundException("User not Found"));

        // Enable the user account
        user.setEnabled(true);
        userRepository.save(user);

        // Mark the token as validated by setting the validation timestamp
        savedUserToken.setValidatedTokenAt(LocalDateTime.now());
        tokenRepository.save(savedUserToken);
    }

}
